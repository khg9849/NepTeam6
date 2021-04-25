import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class paintClient {
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private final String serverIP="127.0.0.1";
	private final int port=9790;
	
	
	private JFrame f;
	private BufferedImage bi;
	private JLabel l;
	private Brush b;

	private boolean isThread=true;
	
	class WinEvent implements WindowListener{ 
		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			paintDTO dto=new paintDTO();
			dto.setCommand(Info.EXIT);
			try {
				writer.writeObject(dto);
				writer.flush();
				System.out.println("������ EXIT ����");
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			isThread=false;
			
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
	
	public void setCanvas() {
		//System.out.println("Setting canvas...");
		f=new JFrame("Canvas");
		f.setSize(500,500);
        f.setLocationRelativeTo(null);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);
        f.setVisible( true );
        f.addWindowListener(new WinEvent());
   
        bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
        //BufferedImage: �̹��� �ȼ� ������ ���ۿ� ������ �̹����� ó���ϴ� Ŭ����
        
        l = new JLabel(new ImageIcon(bi) );
        l.setBounds(20,20,400,400);
        f.add(l);
        
        //�귯�� ����
    	Brush bb=new Brush(0,255,0);
        bb.setBounds(20,20,400,400);
        f.add(bb);
        
        
        l.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
            	
                //�귯�ø� ȭ�鿡 �ǽð� ����
            	 bb.setXx(e.getX());
                 bb.setYy(e.getY());
                 bb.repaint();
                 bb.printAll(bi.getGraphics());
                 
                 //�귯�ø� dto�� �־ ����
                 paintDTO dto=new paintDTO();
                 dto.setB(bb);
                 
				try {
					writer.writeObject(dto);
					writer.flush();
					writer.reset();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
            }
            
            public void mouseMoved(MouseEvent e) {

            }           
        }); 
	}
	
	public void recvData() {
		Thread recvThread=new Thread(new Runnable() {

			@Override
			public void run() {
				while(isThread) {
					try {
						//dto ����
						paintDTO dto=(paintDTO)reader.readObject();
						
						//dto�� �ִ� �귯�� ��ü�� ����
						Brush recvbb=dto.getB();
						recvbb.setBounds(20,20,400,400);
					     f.add(recvbb);
					     
					     //�ǽð� ������ �귯�ø� ���������� �ǽð� ����
					     recvbb.update(bi.getGraphics());
					     recvbb.repaint();
					     recvbb.printAll(bi.getGraphics());
					     
					}catch(IOException e) {
						e.printStackTrace();
					}catch(ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
				try {
					reader.close();
					writer.close();
					socket.close();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
			
		});
		recvThread.setDaemon(true);
		recvThread.start();
	}
	

	
	public paintClient() {
		try {
			socket=new Socket(serverIP,port);
			writer=new ObjectOutputStream(socket.getOutputStream());
			reader=new ObjectInputStream(socket.getInputStream());
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		setCanvas();
		System.out.println("setting is done");
		recvData();
		
	}
	public static void main(String[] args) {
		new paintClient();
	}
}
