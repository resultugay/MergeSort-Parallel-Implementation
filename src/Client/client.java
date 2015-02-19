
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextArea;


/**
 *
 * @author resultugay
 */
public class client {
    public static JTextArea generalArea;
    public static String serverName = "127.0.0.1";   //Server'ın IP adresi
    public static int port = 1234;                   //Server'ın port numarası
    static Socket server;
    
      static InputStream inputStream;
      static DataInputStream dataInputStream;          //Veri akışını yapacak giriş çıkış kanallarımız
      static OutputStream outpuStream;
      static DataOutputStream dataOutputStream;
    
    public client(){
    JFrame frame = new JFrame("Merge Sort Parallel");
    frame.setSize(700, 600);
    frame.setLocation(300, 75);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    generalArea = new JTextArea();
    generalArea.setSize(500, 400);
    generalArea.setLocation(100, 75);
    generalArea.setEditable(false);
    frame.getContentPane().add(generalArea);
    frame.setVisible(true);
    }
      
    public static void main(String [] args){   // client için thread oluşturuluyor.
        client client = new client();
        Thread client2 = new Thread(new clientRunnable());
        client2.start();
            
    }
  
}
