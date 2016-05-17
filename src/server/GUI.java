
package server;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class GUI extends JPanel{
    static int xdev,ydev;
    static boolean safe;
    public GUI(int x, int y,boolean state){
        xdev=x;
        ydev=y;
        safe=state;
    }


   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      //g.fillRect(0,0,800,600);
      
      setBackground(new Color(204, 204, 204));
    
    //g.drawRect(0, 0, 800, 600);
    Color roomc=new Color(119, 192, 217);
    Color bathc=new Color(229, 237, 157);
    Color statc=new Color(0, 255, 47);
      g.setColor(roomc);
    g.fillRect(0, 0, 1000, 800);
    g.setColor(statc);
    g.fillRect(1050, 350, 100, 100);
    if(!safe){
        g.setColor(Color.RED);
        g.fillRect(1050, 350, 100, 100);
    }
        
    g.setColor(bathc);
    g.fillRect(0, 0, 400, 300);
    g.setColor(Color.red);
    g.fillOval(xdev-15, ydev-15, 30, 30);
    
    g.setColor(Color.BLUE);
   }
    
    
}
