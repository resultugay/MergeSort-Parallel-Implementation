
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
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author resultugay
 */
public class runClients extends Thread{
    Socket client ;        //Diğer sınıftaki ile aynı mantıkta oluşturuldu.Kullanıcı dağıtılan dizileri sıralanmış
    int hangiClient = 0;   //şekilde toplamak için bu threadi kullanır.
   private int array [];
  
   public static InputStream inputStream ;       //Veri alabilmek için akış yolu
   public static DataInputStream dataInputStream;//Formatlı(int,byte) veri alabilmek için
   public static OutputStream outputStream;      //Veri gönderebilmek için akış yolu  
   public static DataOutput dataOutputStream;    //Formatlı veri gönderebilmek için    
   
   
    public runClients(){
       client = new Socket();
    }
    public void run(){
        
        try {
            inputStream = client.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            outputStream = client.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            
            dataOutputStream.writeUTF("gönder");
            array = new int[server.limit/server.beklenenClientSayisi];
            for(int i = 0 ; i < array.length ; i++)
            {
                array[i] = dataInputStream.readInt();
                //System.out.println("alınan eleman" + array[i]);
            }
            generalArea.setText(generalArea.getText() + "\n------------\n Client " + hangiClient + " "
                     + "sıralanmış veri alındı");
            
            Thread.currentThread().interrupt();

        } catch (IOException ex) {
            Logger.getLogger(runClients.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    }
    
    public void setHangiClient(int hangiClient){
        this.hangiClient = hangiClient;
    }
    
    public int [] getArray(){
        return array;    
    }    
    
    
    
}
