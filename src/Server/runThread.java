
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

package Server;

import static Server.server.clients2;
import static Server.server.generalArea;
import static Server.server.number_of_client;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author resultugay
 */
public class runThread extends Thread{

   static int deger;
   static double seconds;
   static long start,end,elapsedTime; // zamanı ölçmek için gerekli parametreler.
  
    
   @Override
    public void run(){
        start = System.nanoTime();                   //ölçüm yapmak için start başlatılıyor.
        for(int i = 0 ; i < number_of_client ; i++){ //Verileri almak için her client'ın threadi başlatılıyor.
            try {
               clients2[i].setHangiClient(i);
               clients2[i].start();
               clients2[i].join();
            } catch (Exception ex) {
                Logger.getLogger(deployThread.class.getName()).log(Level.SEVERE, null, ex);
            }
          
        }
        end = System.nanoTime();
        elapsedTime = end - start;
        seconds = (double)elapsedTime / 1000000000.0; // nanosaniye cinsinde olduğu için 1 milyara bölünüyor.
        generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "sistem toplamaya " + seconds + "  saniye harcadı");        
        server.seconds = server.seconds + seconds;
        
        start = System.nanoTime();                                    //tüm clientlardan gelen veriler tek bir dizide birleştirildi.
        int a [] = new int[clients2[0].getArray().length * clients2.length];//çünkü mergesort mantığı ile toplama yapılacak.
        for(int j = 0 ; j < clients2.length  ; j++){                    //Bu yüzden yukardan aşağı ağaç şeklinde gidilecek.
            System.arraycopy(clients2[j].getArray(), 0, a, j*clients2[0].getArray().length, clients2[j].getArray().length);
        }
        end = System.nanoTime();
        elapsedTime = end - start;
        seconds = (double)elapsedTime / 1000000000.0;
      //  System.out.println("dizi birleştirildi" + seconds + " saniyede");
         generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "dizi birleştirildi " + seconds + "  saniyede");        
        server.seconds = server.seconds + seconds;
     
        start = System.nanoTime();                
        sort(a,clients2.length);              //clientlardan gelen verilen sıralama metodumuza girerek yeniden birleştiriliyor
        end = System.nanoTime();
        elapsedTime = end - start;
        seconds = (double)elapsedTime / 1000000000.0;
        generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "dizi sıralandı" + seconds + "  saniyede");  
        System.out.println(seconds);
        
        start = System.nanoTime(); 
        PrintWriter writer2;                  //elde edilen sonuç printwriter ile dosyaya yazılıyor.
       try {
           writer2 = new PrintWriter("D:\\sirali.txt", "UTF-8");
                   for(int i = 0 ; i < a.length ; i++)
           writer2.println(a[i]);
        writer2.close();        
        end = System.nanoTime();
        elapsedTime = end - start;
        seconds = (double)elapsedTime / 1000000000.0;
        generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "dizi dosyaya yazıldı " + seconds + "  saniyede");  
        server.seconds = server.seconds + seconds;
        generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "toplam su kadar sure harcandı " + server.seconds + "   saniyede");         
        
       } catch (FileNotFoundException ex) {
           Logger.getLogger(runThread.class.getName()).log(Level.SEVERE, null, ex);
       } catch (UnsupportedEncodingException ex) {
           Logger.getLogger(runThread.class.getName()).log(Level.SEVERE, null, ex);
       }

        
        
         
    }
    
    /*
        Clientlardan gelen verileri birleştirmek için önce hepsi bir arrayda topladım bunlar kendi içeresinde sıralanmış fakat
        dosya genel anlamıyla sıralı değil.Şöyleki ; 10 elamanlı bir dizimiz olsun(implementte bu milyar civarı)
        a [] = {1,5,2,3,4,7,5,2,9,1} ve 2 client olsun 1. clienta deploy butonu ile dizimizin ilk 5 elemanı yani
        {1,5,2,3,4} ve 2 . client'a ise sonraki 5{7,5,2,9,1} eleman yollanır.Clientlar bunları kendi aralarında bağımsız olarak
        sıralar ve geri yollarlar.
        1. client'tan gelen = {1,2,3,4,5}
        2. client'tan gelen = {1,2,5,7,9}
        
        daha sonra clientlardan gelen bu veriler serverda önce birleştirilirler şu şekilde
        array [] = {1,2,3,4,5,1,2,5,7,9} dikkat edilirse bu dizi iki parça olarak düşünülürse sıralı fakat bütün
        olarak sıralı değil.İşte bu noktada geliştirmiş olduğum merge algoritması bunları tekrar ortadan birleştirerek yukarı doğru
        birleştirir.Bunu algoritmaya derinlik bilgisi katarak yaptım.Şöyleki.
                                /(root) 1.seviye
                              / \       2.seviye
                            /\  /\      3.seviye
            Client sayısına göre derinlik artmakta olduğu için merge metodu içindeki
            derinlik--; 
           if(hi <= lo || derinlik == 0 ) return;
        kodları yardımıyla dizi kaç elamanlı olursa olsun client sayısı kadar derinliğe ulaştığı anda return yapar
        dolayısıyla siz 1024 elamanlı bir diziyi tekrarda sıralamak zorunda değilsiniz bu yöntemle zaten sıralı olan bölgeleri
        en fazla 10 adımda birleştirebilirsiniz.Buradan da görülüyor ki paralel olarak çok fazla zaman tasarrufu var.
    
    
    */
    
    
        
      public static void sort(int[]a,int derinlik){ 
       int[] aux = new int[a.length];
       sort(a,aux,0,a.length - 1,derinlik);
   }
    
    private static void sort(int[]a,int[]aux,int lo,int hi,int derinlik){
       derinlik--; 
       if(hi <= lo || derinlik == 0 ) return;
       int mid = lo + (hi - lo)/2;
       sort(a,aux,lo,mid,derinlik);
       sort(a,aux,mid+1,hi,derinlik);
       merge(a,aux,lo,mid,hi);           
       
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
