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
				System.out.println("서버에 EXIT 전송");
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
		System.out.println("Setting canvas...");
		f=new JFrame("Canvas");
		f.setSize(500,500);
        f.setLocationRelativeTo(null);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);
        f.setVisible( true );
        f.addWindowListener(new WinEvent());
   
        bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
        //BufferedImage: 이미지 픽셀 정보를 버퍼에 저장해 이미지를 처리하는 클래스
        
        l = new JLabel(new ImageIcon(bi) );
        l.setBounds(20,20,400,400);
        f.add(l);
        
        
        l.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
            	System.out.println("mouseEvent occured");
            	
            	Brush bb=new Brush();
                bb.setBounds(20,20,400,400);
                f.add(bb);
                
            	 bb.setXx(e.getX());
                 bb.setYy(e.getY());
                 bb.repaint();
                 bb.printAll(bb.getGraphics());
                 paintDTO dto=new paintDTO();
                 dto.setB(bb);
                 
				try {
					writer.writeObject(dto);
					writer.flush();
					
					System.out.println("we drew...");
	                dto.getB().print();
					System.out.println("send picture to server");
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
						paintDTO dto=(paintDTO)reader.readObject();
						System.out.println("reading...");
						
						
						Brush bb=dto.getB();
					     
					     System.out.println("we received...");
					     bb.print();
					     
					     f.add(bb);
					     bb.update(bi.getGraphics());
					     bb.repaint();
					     bb.printAll(bi.getGraphics());
						System.out.println("painting is done");
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
