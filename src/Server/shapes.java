/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.TextArea;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author resultugay
 */
public class shapes extends JPanel{
    int x1 ,y1 ,x2 ,y2,k;  
    JPanel a;
    
    public shapes(JPanel a){
    this.a = a;
    x1 = 150;
    y1 = 50 ; 
    x2 = 0;
    y2 = 250;
    }
    
    
    @Override
      public void paintComponent(Graphics g){     
 
        g.drawLine(x1, y1, x2, y2);
           
        } 
    
    public void draw(){    
        a.setBackground(Color.white);
       for(int i = 0 ; i < server.beklenenClientSayisi ; i++){
           x2 = (i + 1)*300/(server.beklenenClientSayisi+1) ;
           sleep();}
    }      
      public void sleep(){
        repaint();   //Her repaint metodu çağrımında paintComponent metodu çağrılır              
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(shapes.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
}
