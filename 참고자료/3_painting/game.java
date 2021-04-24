


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.*;

public class game{    

	
	public static void main(String[] a) {
        
        JFrame f = new JFrame("Canvas");
        f.setSize(800,800);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);
        f.setVisible( true );
                
        BufferedImage bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB );
        //BufferedImage: �̹��� �ȼ� ������ ���ۿ� ������ �̹����� ó���ϴ� Ŭ����
        
        JLabel l = new JLabel(new ImageIcon(bi) );
        l.setBounds(20,20,500,500);
        f.add(l);
        
        // �귯����� Ŭ������ �����Ѵ�. �Ʒ��ʿ� ������.
        Brush b = new Brush();
        b.setBounds(20,20,500,500); //ũ�Ⱑ l�� ���ƾ� �Ѵ�.
        f.add(b);
        
        
        // ���콺 ��� �̺�Ʈ
        l.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
                
                b.xx=e.getX();
                b.yy=e.getY();
                b.repaint();
                b.printAll(  bi.getGraphics() ); //�귯���� BufferedImage �� �׸���.    
                
                b.x = b.xx;
                b.y = b.yy;
            }
            
            public void mouseMoved(MouseEvent e) {

            }           
        });
        // ���콺 �Է� �̺�Ʈ
        l.addMouseListener(new MouseAdapter() {
        
        	public void mousePressed(MouseEvent e) {
        		b.x = e.getX();
        		b.y = e.getY();
        	}
        });
        
    }   
}


class Brush extends JLabel{
       
	public int x, y;
    public int xx, yy;
    
    public Color col=new Color(255,0,0);
    
    public void paint(Graphics g) {     
    
        g.setColor( col );
        g.fillOval( xx-10, yy-10, 20, 20);
        
        //���� ���� , ������ ��� ����
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(20, 0, 1, 0));
        
        //���� ��ġ�� ���� ��ġ�� ������ �ձ�
        g.drawLine(this.x, this.y, this.xx, this.yy);
    }

}
