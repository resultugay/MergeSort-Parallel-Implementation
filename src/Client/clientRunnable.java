
/*
                                          2013-2014 Tasarım Projesi
Yapan     : Resul Tugay 
Danışman  : Öğr.Gör. Selçuk Cevher
Proje adı : Paralel Hesaplama ile MergeSort
Tanım     :  Günümüzde artan kullanıcı ve işlem sayısı ile birlikte hızın dahada önem kazandığı dönemde
            tek işlemcili bilgisayarlar artık görevleri istenilen hızda yerine getirememektedir.Bu yüzden
            paralel bilgisayarlar kullanılarak bir problem alt parçalara bölünüp daha hızlı implemente 
            edilip daha hızlı sonuçları üretebilir.Bu proje en güçlü sıralama algoritmalarından 
            Jon Van Neumanın 1945 te geliştirdiği merge-sort algoritmasının paralel olarak runtime da
            istenilen(2 ve katı olmak zorunda Ağaç yapısında dolayı) client sayısı kadar server client
            şeklinde dağıtılıp sonrada aynı mantıkta toplanıp merge edilme işlemidir.Amaç merge-sort 
            kullanılarak sıralamanın daha hızlı yapılmasıdır.

 */

package Client;

import static Client.client.generalArea;
import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author resultugay
 */
public class clientRunnable extends Thread{
    // public static String serverName = "192.168.1.39";//Server'ın IP adresi
     public static int port = 1234;                   //Server'ın port numarası
     static Socket server;
     
      static InputStream inputStream;
      static DataInputStream dataInputStream;          //Veri akışını yapacak giriş çıkış kanallarımız
      static OutputStream outpuStream;
      static DataOutputStream dataOutputStream;
      
      int ToplamclientSayisi = 0;
      int hangiClient = 0;
      int limit = 0;
      String temp;
      List list ;
    public clientRunnable(){
         try {
             //server = new Socket(serverName, port);
            // System.out.println(InetAddress.getLocalHost().getHostAddress());
             server = new Socket(InetAddress.getLocalHost().getHostAddress(), port);
             generalArea.setText("Server ile bağlantı kuruldu");
             inputStream = server.getInputStream();
             dataInputStream  = new DataInputStream(inputStream);      //Anlamlı veri akışı için
             outpuStream = server.getOutputStream();
             dataOutputStream = new DataOutputStream(outpuStream);
             
             } catch (IOException ex) {
             //Logger.getLogger(clientRunnable.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(null, "bağlanılamadı!");
         }
    }
    
    public void run(){
         try {
             temp = dataInputStream.readUTF();    // server tarafından gönderilen ilk bilgileri almak için temp kullannıldı.
             hangiClient = Integer.parseInt(temp.split(" ")[0]);//sonra bu temp split edilerek parçalandı.
             hangiClient++; 
             ToplamclientSayisi = Integer.parseInt(temp.split(" ")[1]);
             limit =  (Integer.parseInt(temp.split(" ")[2]));
             generalArea.setText(generalArea.getText() + "\nSistemde ben " + hangiClient + ".Client'ım"
             + "\nVe sistemde toplam " + ToplamclientSayisi + " tane client var"
             + "\nVe dizinin boyutu = " + limit);
             
             int [] array = new int[limit/ToplamclientSayisi];  // serverdan gelen verilen arrayda tutuldu.Sistemdeki limit n ise
             for(int i = 0; i < array.length ; i++){           // ve sistem toplam m tane client varsa her client n/m lik parçayı
                 array[i] = dataInputStream.readInt();         //sıralar.
             }
             generalArea.setText(generalArea.getText() + "\n dizi alındı");
           /*  PrintWriter writer2 = new PrintWriter("D:\\" + hangiClient +".txt", "UTF-8"); // alınan dizilerin kontrolü için.
                 for(int i = 0 ; i < array.length ; i++)
                 { writer2.println(array[i]);}                 
                 writer2.close();*/
             sort(array);                                               // dizi merge sort yardımıyla sıralandı.
              generalArea.setText(generalArea.getText() + "\n ----------------------\n"
                      + "Alınan veri sıralandı");
           /*   PrintWriter writer2 = new PrintWriter("D:\\" + hangiClient +".txt", "UTF-8");
                 for(int i = 0 ; i < array.length ; i++)
                 { writer2.println(array[i]);}                 
                 writer2.close();*/
              String temp2 = dataInputStream.readUTF();   //serverdan gönder emri gelirse
             if(temp2.equals("gönder")){                  // sıralanmış dizi gönderilecek.
                     generalArea.setText(generalArea.getText() + "\n ----------------------\n"
                      + "Gönder emri geldi");
                 for(int i = 0; i < array.length ; i++){   //sıralanmış dizi gönderiliyor.
                     dataOutputStream.writeInt(array[i]);
                    // System.out.println(array[i]);
                 }
             } 
                 generalArea.setText(generalArea.getText() + "\n ----------------------\n"
                      + "Veri sıralandı ve gönderildi");
           
            
         } catch (IOException ex) {
            // Logger.getLogger(clientRunnable.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(null, "Server Could not find!!");
             System.exit(0);
         }
    
    }
    
     private static void merge(int[] a,int [] aux,int lo,int mid,int hi){
     assert  isSorted(a,lo,mid);
     assert  isSorted(a,mid+1,hi);
     
     for(int k = lo ; k <= hi ; k++)
     {
         aux[k] = a[k];                     //copy
     }
            
     int i = lo , j = mid + 1;                           //merhe
     
     for(int k = lo ; k <= hi ; k++){
        if( i > mid)                 a[k] = aux[j++];
        else if( j > hi )            a[k] = aux[i++];
        else if(less(aux[j],aux[i])) a[k] = aux[j++];
        else                         a[k] = aux[i++];        
           
    }
     
     assert isSorted(a,lo,i);
        
    }
    
   private static void sort(int[]a,int[]aux,int lo,int hi){
   
       if(hi <= lo ) return;
       int mid = lo + (hi - lo)/2;
       sort(a,aux,lo,mid);
       sort(a,aux,mid+1,hi);
       merge(a,aux,lo,mid,hi);           
       
   } 
   public static void sort(int[]a){
       int[] aux = new int[a.length];
       sort(a,aux,0,a.length - 1);
   }
   
     private static boolean less(Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }
    
     private static boolean isSorted(int[] a) {
       return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(int[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }
    
        private static void show(byte[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }    
    
    
    
    
    
    
    
    
}
