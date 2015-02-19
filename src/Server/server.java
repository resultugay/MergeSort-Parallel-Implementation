
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

/*
        Programda görsel arayüz kullanıldığı için Java'nın awt ve bir üst sürümü olarak
        kabul edilen swing paketleri import edildi.Ayrıca Programda Scanner sınıfı
        verilen dosyadan okuma yaptığı için Java'nın input output sınıfı import edildi.
        
*/
import static Server.server.DeployButton;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author resultugay
 */
public class server implements ActionListener{
    //Diğer sınıflarında erişim işlem yapabilmesi amacıyla
    //Görsel arayüz elemanları public ve static olarak tanımlandı.
    public static JTextArea generalArea;
    public static JPanel panel;
    public static JTextArea clientsArea;
    public static JButton DeployButton;
    public static JButton runButton;
    public static JScrollPane scrollPane;
    public static JScrollPane scrollPane2; 
   //String IPAdress = "192.168.195.103"; //Server ile haberleşilecek IP
    int port = 1234; //Server ile haberleşilecek port numarası
   
    static   ServerSocket serverSocket ; //Server için socket
    static boolean deploy_active = true; //Beklenilen client sayısı kadar client geldiğinde client beklenilmeyecektir.
    static int number_of_client = 0; //elde edilen client sayısı bir artırılarak işlem yapılır.
    public static int beklenenClientSayisi;
    
    static Clients clients [] ; // dağıtmak için kullanılan client bilgileri tutan client sınıf
    static runClients clients2 []; // birleştirmek için farklı thread kullanmak amacıyla kullanılar 
                                   // ve run butonuyla ilişkili clientleri tutan diğer thread sınıfı. 
   static int limit = 100000; //Sıralanacak dizinin boyutu
   static String dosyaYolu= "D:\\"+ limit +".txt"; // Önceden oluşturulan dizindeki dosyalar alınıyor dosyayolu olarak
   static Scanner scanner; //Belirtilen dosyadan scanner nesnesi yardımıyla okunma yapmak için tanımlandı.
   static int array [];//Dosyadan okunacak olan dizi bu diziye atanıyor.
 
   
   static long start,end,elapsedTime; // zamanı ölçmek için gerekli parametreler.
   static double seconds;   
   
   static JFrame frame;

    public server(){                                                     //Constructor'da genel olarak frame ayarlanıyor
        frame = new JFrame("Merge Sort Parallel Implementation"); //Frame başlığı setleniyor.
        frame.setSize(700, 600);                                         //Frame boyutu ayarlanıyor                
        frame.setLocation(300, 75);                                      //Framein yeri yaklaşık olarak ekranın ortası
        buildGUI(frame.getContentPane());                               //Bu methodda komponentler frame eklenip ayarlanıyo
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);          //Eğer program kapatılırsa arkaplanda çalışmaması için
        try {
            serverSocket = new ServerSocket(port);                      //dinlemeyi başlatıyoruz
        } catch (IOException ex) {
           // Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Server could not open!!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        generalArea.setText("Client bekleniyor.."); 
        
    }
    public void buildGUI(Container container){
        
        generalArea = new JTextArea();
        generalArea.setSize(300, 300);
        generalArea.setLocation(100, 50);
        generalArea.setEditable(false);
        
        panel = new JPanel();
        panel.setSize(300, 300);
        panel.setLocation(100, 50);
        
        
        clientsArea = new JTextArea();
        clientsArea.setSize(170, 300);
        clientsArea.setLocation(470, 50);
        clientsArea.setEditable(false);
        
        scrollPane = new JScrollPane(generalArea);
        scrollPane.setSize(300, 300);
        scrollPane.setLocation(100, 50);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane2 = new JScrollPane(clientsArea);
        scrollPane2.setSize(170, 300);
        scrollPane2.setLocation(470, 50);
        scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        DeployButton = new JButton("Dağıt");
        DeployButton.setLocation(200, 380);
        DeployButton.setSize(120, 70);
        DeployButton.setEnabled(false);
        DeployButton.addActionListener(this);
        
        runButton = new JButton("Topla");
        runButton.setLocation(350, 380);
        runButton.setSize(120,70);
        runButton.setEnabled(false);
        runButton.addActionListener(this);
        
        container.setLayout(null);
        container.add(panel);
        container.add(scrollPane2);
        container.add(DeployButton);
        container.add(runButton);
        
    }
    
    
    public static void main(String [] args) throws FileNotFoundException{
          // gerekli client sayısı gelene kadar inaktif olacak butonumuz.
        server server = new server();
        String tmp = JOptionPane.showInputDialog("Kaç tane istemci bekleniyor?");
        if(tmp == null) // kullanıcıdan client sayısı alınıyor eğer verilmezse 4'e setleniyor.
           tmp = "4";
        beklenenClientSayisi = Integer.parseInt(tmp);//Client sayısına göre bekleme
        generalArea.setText(generalArea.getText() + "\n---------------\n"                       //yapıyoruz gerekli beklenen client
                + beklenenClientSayisi + " Client bekleniyor ");                               //client gelirse dağıtma işlemi
        String tmp2 = JOptionPane.showInputDialog("Size of array?");                                                                                       //yapılabilir.
        if(tmp2 == null)   // sıralanacak dizi soruluyor.Eğer setlenmezse 100 elemanı olan dizi alınacak.
            tmp2 = "100";
        limit = Integer.parseInt(tmp2);
     
        shapes a = new shapes(panel);
        a.setLocation(100, 50);
        a.setSize(300,300);
        frame.getContentPane().add(a);
        a.draw();
               
        dosyaYolu =  "D:\\"+ limit +".txt";
        
        array = new int[limit];                        //belirtilen dosya limitinde dizi oluşturuluyor
        scanner = new Scanner(new File(dosyaYolu));   //Belirtilen dosyadan okuma yapacak scanner nesnesi.
        
        for(int i = 0 ; i < array.length; i++){      //belirtilen limit kadar veriler dosyadan okunup parçalanıp dağıtılmak üzere
            array[i] = scanner.nextInt();            //diziye aktarılıyor
        }
        
          generalArea.setText(generalArea.getText() + "\n---------------\n" + limit
        + " boyutlu dizi okundu "); 
              
         clients = new Clients[beklenenClientSayisi];    //Client sınıfındaki constructor'ı başlatmak için beklenen client sayisi
            for(int i = 0 ; i < beklenenClientSayisi ; i++) //oluşturuluyor.
           clients[i]= new Clients();
            
        clients2 = new runClients[beklenenClientSayisi];
            for(int i = 0 ; i < beklenenClientSayisi ; i++)
           clients2[i]= new runClients();
          
    /*
            Beklenilen(ilk başta JOptionPane dialog ile sorulmuştu) client sayısını istenilen sayıya ulaştığı anda
            deploy_active değişkeni false yapılarak artık client bekleme işlemi sona erdiriliyor.
            Bu while döngüsünde her gelen client nesnesi client sınıfında başlatılarak
            birden fazla client'ın hafızada kalmasını sağlıyor.
            
    */                    
       
        while(deploy_active){
            try {
                
                Clients client = new Clients();
                client.client =  serverSocket.accept(); // Client gelene kadar kod burada kalır eğer client gelirse kabul edilir ve
                clients[number_of_client] = client;   //sonraki kod satırları icra edilir.Burada number_of_client değişkeni ile
                                                      //hangi clientin hangi nesne indisinde tutulduğunu belirliyoruz.
                runClients ca = new runClients();     //problemi dağıttıktan sonra toplamamız için runClient sınıf içinde gerekli
                ca.client = client.client;            //setlemeleri yapıyoruz.Farklı threadlerde yapıldığı için bu işlem gerekli.
                clients2[number_of_client].client = ca.client;
                
                } catch (IOException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
            number_of_client++; // her gelen client ile number_of_client değişkeni bir arttırılıyor.
            generalArea.setText(generalArea.getText() + "\n------------\nClient " + number_of_client + "  geldi.");
            clientsArea.setText(clientsArea.getText() + "\n" + clients[number_of_client-1].client.getRemoteSocketAddress());
            if(number_of_client == beklenenClientSayisi){ // eğer gelen client sayısı beklenilen client sayısına eşitse
                deploy_active = false;                   //Server client beklemekten vazgeçiyor.
                DeployButton.setEnabled(true);
                runButton.setEnabled(true);
                break;
            }
        }
       JOptionPane.showMessageDialog(null, "Clients are okay!! Please Deploy the problem", "Information", JOptionPane.INFORMATION_MESSAGE);
       
    } 

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == DeployButton){
           /*for(int  i = 0; i < number_of_client ; i++)  // eğer for metodu kullanılırsa deploy tuşu basıldığı anda tüm clientlara
            {                                              //hizmet gidinceye kadar buton basılı kalır bunun önüne geçmek için              
                try {                                      //Her clientın bağlı olduğu threadı başlatmak için yeni bir thread sınıfı
                    clients[i].setHangiClient(i);         //oluşturuldu ve o sınıfın run metodu yardımıyla dağıtma işlemi yapıldı
                    clients[i].start();                   //böylelikle buton basılı kalmayacak.
                    clients[i].join(); 
                } catch (Exception ex) {
                    Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
            deployThread a = new deployThread();          //Problemi bölüp dağıtan sınıf.
            a.start();                                    //Threadi başlatıyoruz.

        generalArea.setText(generalArea.getText() + "\n------------\nDosya " +
               number_of_client + " parçaya bölündü ve dağıtılıyor..!");        
        }
        if(e.getSource() == runButton) {
                runThread thr =new runThread();          //toplamak için gerekli run thread sınıfı nesnesi oluşturuluyor.
                thr.start();                             //thread başlatılıyor.
        }
        
    }
    
}
