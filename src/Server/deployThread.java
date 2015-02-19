
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

import java.util.logging.Level;
import java.util.logging.Logger;
import static Server.server.*;
/**
 *
 * @author resultugay
 */
public class deployThread extends Thread{
    /*
        Deploy butonu takılı kalmasın diye yeni bir thread ile her client a ait olan threadler başlatılmak için bu sınıf tasarlandı.
       
    */
    
   
    
    @Override
    public void run(){       
        
          start = System.nanoTime();         
              for(int i = 0 ; i < number_of_client ; i++){
            try {
                clients[i].setHangiClient(i);
                clients[i].start();
                clients[i].join();
            } catch (Exception ex) {
                Logger.getLogger(deployThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        end = System.nanoTime();
        elapsedTime = end - start;
        seconds = (double)elapsedTime / 1000000000.0; // nanosaniye cinsinde olduğu için 1 milyara bölünüyor.
        //System.out.println("sistem toplama " + seconds + " saniye harcadı");  
         generalArea.setText(generalArea.getText() + "\n------------\n "
                    + "sistem dağıtmaya " + seconds + "  saniye harcadı");       

    }
    
}
