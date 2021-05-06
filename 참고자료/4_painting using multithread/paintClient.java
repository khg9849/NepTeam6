import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

//���� ������ ��Ʈ���ϴ� Brush�� ����
enum BrushMode{
	DRAW, ERASE
}

public class paintClient {
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private final String serverIP="127.0.0.1";
	private final int port=9790;
	
	private JFrame f;
	
	private JLabel canvas;
	private BufferedImage bi;
	private JMenuBar mb;
	
	// color picker
	private myColorPicker colorPicker;
	private Color col;
	
	// brush diameter(����)
	private JSlider diaCol;
	
	//���� �귯���� ���� (e.g. �׸�����, �������, ���������� ...)
	private BrushMode brushMode;
	private JRadioButton rb_draw;
	private JRadioButton rb_erase;
	
	private boolean isThread=true;
		
	public void initSlider() {
		diaCol = new JSlider(JSlider.HORIZONTAL, 1, 100, 20);
		diaCol.setBounds(450,20, 100,30);
        f.add(diaCol);
	}
	
	public void initMenu() {
        mb=new JMenuBar();
    	JMenu showMenu=new JMenu("Show");
    	JMenu fileMenu=new JMenu("File");
    	JMenu tempMenu=new JMenu("Temp");
    	mb.add(showMenu);
    	mb.add(fileMenu);
    	mb.add(tempMenu);
    	
    	JMenuItem item1=new JMenuItem("show color picker");
    	item1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    				colorPicker.appear();
			}
    	});
    	showMenu.add(item1);
    	
    	JMenuItem item2=new JMenuItem("Fetch");
    	item2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			paintDTO dto=new paintDTO();
                dto.setCommand(Info.FETCH);
				try {
					writer.writeObject(dto);
					writer.flush();
					writer.reset();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
    		}
    	});
    	tempMenu.add(item2);
    	
    	JMenuItem item3=new JMenuItem("Save");
    	item3.addActionListener(new ActionListener() {
    		 public void actionPerformed(ActionEvent e) {
    			 
                 try { 
                	 String fileName=JOptionPane.showInputDialog("������ ���� �̸��� �Է��ϼ���");
        			 ImageIO.write(bi, "PNG", new File("./src/"+fileName+".PNG")); 
        			 int result=JOptionPane.showConfirmDialog(item3, "������ ����Ǿ����ϴ�. ������ ���ðڽ��ϱ�?","Confirm",JOptionPane.YES_NO_OPTION);
        			 if(result==JOptionPane.YES_OPTION)
        				 Desktop.getDesktop().open(new File("./src"));	 
                 }
                 	
                 catch (IOException e1) { e1.printStackTrace(); }               
              }            
          });
    	fileMenu.add(item3);
    	
    	f.setJMenuBar(mb);
    	 
	}
	
	public void initCanvas() {
		 bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
		 
	        //BufferedImage: �̹��� �ȼ� ������ ���ۿ� ������ �̹����� ó���ϴ� Ŭ����
	        Graphics g=bi.getGraphics();
	        g.setColor(Color.WHITE);
	        g.fillRect(0, 0, 400, 400);
	        canvas=new JLabel(new ImageIcon(bi));
	        canvas.setBounds(20,20,400,400);
	        f.add(canvas);
	}
	
	public void initRadioButton() {
		//���� ��ư ���� �� �� ��ư�� ���� �̺�Ʈ ó��
		rb_draw = new JRadioButton("test_draw (B)");
		rb_draw.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				brushMode = BrushMode.DRAW;
			}
		});
		rb_erase = new JRadioButton("test_erase (E)");
		rb_erase.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				brushMode = BrushMode.ERASE;
			}
		});
		
		//�ʱ� ������ draw���
		rb_draw.setSelected(true);
		rb_draw.setFocusable(false);
		rb_erase.setFocusable(false);
		
		//���� ��ư �׷�ȭ = ���� �ɼǵ� �߿� �ϳ��� �����ϰ� �����
		ButtonGroup b_group = new ButtonGroup();
		b_group.add(rb_draw);
		b_group.add(rb_erase);
		
		//���� ȭ�鿡 ���
		rb_draw.setBounds(450,50, 150,30);
		rb_erase.setBounds(450,80, 150,30);
        f.add(rb_draw);
        f.add(rb_erase);
	}
	
	public void setCanvas() {
		f=new JFrame("Canvas");
		f.setSize(600,500);
        f.setLocationRelativeTo(null);
        f.setLayout(null);
        f.addWindowListener(new WindowAdapter() {
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
        });
      
     
        initMenu();
        initSlider();
        initCanvas();
        initRadioButton();
        
        // color picker
        colorPicker=new myColorPicker(Color.BLACK);
        
        //�귯�� ����
    	Brush bb=new Brush(col, 20);
        bb.setBounds(20,20,400,400);
        
        
        canvas.addMouseMotionListener( new MouseMotionListener() {
            
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
        
        canvas.addMouseListener(new MouseAdapter() {
            
        	
        	public void mousePressed(MouseEvent e) {
        		
        		//set color
        		if(brushMode == BrushMode.DRAW) {
        			bb.setCol(colorPicker.getCol());
        		}
        		else if(brushMode == BrushMode.ERASE) {
        			//bb.setCol(bb.getBackground());
        			bb.setCol(Color.WHITE);
        		}
            	bb.setDia(diaCol.getValue());

            	// ���ο� line�� �Էµȴٴ� info ����
                paintDTO dto=new paintDTO();
                dto.setCommand(Info.LINE_START);
				try {
					writer.writeObject(dto);
					writer.flush();
					writer.reset();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
            	
        	}
        	// ���ο� line �Է��� �Ϸ�Ǿ��ٴ� info ����
        	public void mouseReleased(MouseEvent e) {
        		paintDTO dto=new paintDTO();
                dto.setCommand(Info.LINE_FINISH);
				try {
					writer.writeObject(dto);
					writer.flush();
					writer.reset();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
        	}

        });
        //Ű �̺�Ʈ ����
        canvas.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				
			}

			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				switch(keycode) {
				case KeyEvent.VK_B:
					rb_draw.doClick();
					System.out.println("doClick Brush");
					break;
				case KeyEvent.VK_E:
					rb_erase.doClick();
					System.out.println("doClick Erase");
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				
			}
        	
        });
        //Ű �̺�Ʈ�� ��Ŀ���� ���� �� �ִ� ������Ʈ�� ������ �־�� �Ѵ�.
        canvas.setFocusable(true);
        
        f.setVisible( true );
	}
	
	public void recvData() {
		Thread recvThread=new Thread(new Runnable() {

			@Override
			public void run() {
				while(isThread) {
					try {
						//dto ����
						paintDTO dto=(paintDTO)reader.readObject();
						dto.getB().print();
						
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