
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

import static Server.server.generalArea;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author resultugay
 */
public class Clients extends Thread{
   Socket client ;              //her client için ayrı bir soket açacağımız için socket nesnesi.
   int hangiClient = 0;

        
   public static InputStream inputStream ;       //Veri alabilmek için akış yolu
   public static DataInputStream dataInputStream;//Formatlı(int,byte) veri alabilmek için
   public static OutputStream outputStream;      //Veri gönderebilmek için akış yolu  
   public static DataOutput dataOutputStream;    //Formatlı veri gönderebilmek için    
    
    public Clients(){
           client = new Socket(); //constructor'da her client için yeni bir soket başlatılıyor.
          }
 
    
    public void run(){
         try {             
            inputStream = client.getInputStream();             //Veri gönderilip alınacağı akış kanalları oluşturuluyor.
            dataInputStream = new DataInputStream(inputStream);//ve ilk olarak her client'a yapılacak iş hakkında tek seferlik olan
            outputStream = client.getOutputStream();           // writeUTF metodu yardımıyla o client'ın hangi client ve işi toplam 
            dataOutputStream = new DataOutputStream(outputStream); // kaç clientın yapacağı ve toplam iş yükü bildiriliyor.
            dataOutputStream.writeUTF(hangiClient + " " + server.number_of_client + " " + server.limit);
            
            int k = hangiClient*(server.limit/server.beklenenClientSayisi);//k değişkeni client numarasına göre bölünüyor.
            for(int i = 0; i < server.limit/server.beklenenClientSayisi ; i++){//mesela eğer 1. client iseniz ve toplam 4 tane
                dataOutputStream.writeInt(server.array[i+k]);           //client varsa bu satır yardımıyla dizinin 0 dan 250^ye kadar                //System.out.println(server.array[i+k]);
            }                                                          //olan kısmı dağıtılır.2.client'a 250 ila 500 arasındaki değerler             
        
            generalArea.setText(generalArea.getText() + "\n------------\n Client " + hangiClient + " veri gönderildi");
            
            Thread.currentThread().interrupt();     //işi biten client'ın threadı de sonlanıyor daha sonra diğer thread kanalından 
                                                    //toplama yapılacağı için bu threadin açık kalmasına gerek yoktur.
            } catch (FileNotFoundException ex) {
            Logger.getLogger(Clients.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "file not Found!!");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(Clients.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "DataStreams could not start!");
            System.exit(0);
        }         
         
     }
    
    public void setHangiClient(int hangiClient){ //bu method server tarafında setlenerek client'a hangi client olduğu söylenir.
        this.hangiClient = hangiClient;
    }
    
 }
