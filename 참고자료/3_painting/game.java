

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Color;
  
public class game {    
    
    public static void main(String[] a) {
        
        JFrame f = new JFrame("Canvas");
        f.setSize(500,500);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);
        f.setVisible( true );
                
        BufferedImage bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
        //BufferedImage: 이미지 픽셀 정보를 버퍼에 저장해 이미지를 처리하는 클래스
        
        JLabel l = new JLabel(new ImageIcon(bi) );
        l.setBounds(20,20,400,400);
        f.add(l);
        
        // 브러쉬라는 클래스를 생성한다. 아래쪽에 정의함.
        Brush b = new Brush();
        b.setBounds(20,20,400,400); //크기가 l과 같아야 한다.
        f.add(b);
        
        
        // 마우스 모션 이벤트
        l.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
                
                b.xx=e.getX();
                b.yy=e.getY();
                b.repaint();                            
                b.printAll(  bi.getGraphics() ); //브러쉬를 BufferedImage 에 그린다.                
            }
            
            public void mouseMoved(MouseEvent e) {

            }           
        });
        
    }   
}


class Brush extends JLabel{
        
    public int xx, yy;
    public Color col=new Color(255,0,0);
    
    public void paint(Graphics g) {     
    
        g.setColor( col );
        g.fillOval( xx-10, yy-10, 20, 20);
        
    }   
}
