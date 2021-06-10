import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class myColorPicker extends JFrame{
	private JColorChooser chooser;
	private ColorSelectionModel model;
	private MyColorPanel colPanel;
	
	class WinEvent implements WindowListener{ 
		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			disappear();
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	ChangeListener chooserListener=new ChangeListener(){ 
		@Override
		public void stateChanged(ChangeEvent e) {
			colPanel.col=chooser.getColor();
		}
	};

	public void disappear() {
		this.setVisible(false);
	}
	public void appear() {
		this.setVisible(true);
	}
	
	
	myColorPicker(Color col){
		this.setTitle("Color Picker");
		 this.setSize(new Dimension(600, 350));
		 this.setResizable(false);
		 
		 Dimension frameSize = this.getSize(); // frame size
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // monitor size 우측에 위치
			this.setLocation((screenSize.width - frameSize.width), (screenSize.height - screenSize.height));

       
       
        chooser = new JColorChooser();
        chooser.setColor(col);
        colPanel=new MyColorPanel();
        chooser.setPreviewPanel(colPanel);
        
		model=chooser.getSelectionModel();
		model.addChangeListener(chooserListener);
		this.add(chooser);
	}
	public Color getCol() {
		return chooser.getColor();
	}
	public static void main(String[] args) {
		new myColorPicker(Color.WHITE);
	}
}

class MyColorPanel extends JComponent{
	Color col;
	
	public MyColorPanel() {
		setPreferredSize(new Dimension(300, 80));
	}
	public void paint(Graphics g) {
		g.setColor(col);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
