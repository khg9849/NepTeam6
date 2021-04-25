import java.awt.event.MouseAdapter;
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
import javax.swing.JSlider;

public class paintClient {
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private final String serverIP="127.0.0.1";
	private final int port=9790;
	
	
	private JFrame f;
	//private TextField tf1, tf2, tf3;
	private JSlider rCol, gCol, bCol;
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
	
	public void initRGBSlider() {
		
		rCol = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		gCol = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		bCol = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		
		rCol.setBounds(450,20, 100,30);
		gCol.setBounds(450, 70, 100,30);
		bCol.setBounds(450, 120, 100,30);
        
        f.add(rCol);
        f.add(gCol);
        f.add(bCol);

	}
	
	public void setCanvas() {
		//System.out.println("Setting canvas...");
		f=new JFrame("Canvas");
		f.setSize(600,500);
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

        //RGB 색상값 설정하는 JSlider를 생성
        initRGBSlider();
        
        //브러시 설정
    	Brush bb=new Brush(rCol.getValue(), gCol.getValue(), bCol.getValue());
        bb.setBounds(20,20,400,400);
        f.add(bb);
        
        
        l.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
            	            	
                //브러시를 화면에 실시간 구현
            	 bb.setXx(e.getX());
                 bb.setYy(e.getY());
                 bb.repaint();
                 bb.printAll(bi.getGraphics());
                 
                 //브러시를 dto에 넣어서 보냄
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
        
        l.addMouseListener(new MouseAdapter() {
            
        	public void mousePressed(MouseEvent e) {
        		
        		//JSlider에 있는 r, g, b값 가져와서 브러시에 정보 기입
        		int r = rCol.getValue();
            	int g = gCol.getValue();
            	int b = bCol.getValue();
            	
            	bb.setCol(r,g,b);
            	
        	}
        });
	}
	
	public void recvData() {
		Thread recvThread=new Thread(new Runnable() {

			@Override
			public void run() {
				while(isThread) {
					try {
						//dto 받음
						paintDTO dto=(paintDTO)reader.readObject();
						dto.getB().print();
						
						//dto에 있는 브러시 객체를 꺼냄
						Brush recvbb=dto.getB();
						recvbb.setBounds(20,20,400,400);
					     f.add(recvbb);
					     
					     //실시간 구현된 브러시를 마찬가지로 실시간 재현
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
