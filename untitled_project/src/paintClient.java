import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//���� ������ ��Ʈ���ϴ� Brush�� ����
enum BrushMode{
	DRAW, ERASE
}

public class paintClient{

	
	private Socket socket;

	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	//private ObjectInputStream Entryreader;
	//127.0.0.1
	//118.91.36.52
	private final String serverIP="127.0.0.1";
	private final int port=9790;
	private serialTransform st;
	
	//Client ���� ��ȣ
	private int ClientUID;
	public int getClientUID() {return ClientUID;}
	public void setClientUID(int n) {ClientUID = n;}
	
	private String roomID;
	private String nickname;
	private int userCnt;
	
	private JFrame f;
	
	private JLabel canvas;
	private BufferedImage bi;
	private Graphics g;
	private JMenuBar mb;
	
	private myEntry entry;
	 
	// color picker
	private myColorPicker colorPicker;
	private myChatBoard chatBoard;
	
	private Color col;
	
	// brush diameter(����)
	private JSlider diaCol;
	
	//���� �귯���� ���� (e.g. �׸�����, �������, ���������� ...)
	private BrushMode brushMode;
	private JRadioButton rb_draw;
	private JRadioButton rb_erase;
	
	private boolean isThread=true;
	
	
	public boolean isRecvDataStart = true;
	//LayerList �迭�� BufferImage�� �迭
	//LayerName �迭�� �ش� Layer�� ���� �̸�
	private ArrayList<Layer> layerList;
	private DefaultListModel<String> layerName;
	//private String[] layerName;
	JList<String> jl;
	private int addLayer_initcount = 0;
	private int selected_layerindex = 0;
	
	public void initSlider() {
		diaCol = new JSlider(JSlider.HORIZONTAL, 1, 100, 20);
		diaCol.setBounds(450,20, 100,30);
		diaCol.setFocusable(false);
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
    	
    	JMenuItem item12=new JMenuItem("show chat board");
    	item12.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    				chatBoard.appear();
			}
    	});
    	showMenu.add(item12);
    	
    	//JMenuItem item2=new JMenuItem("Fetch");
    	//item2.addActionListener(new ActionListener() {
    	//	public void actionPerformed(ActionEvent e) {
    	//		paintDTO dto=new paintDTO();
        //        dto.setCommand(Info.FETCH);
		//		try {
		//			writer.writeObject(st.encrypt(dto));
		//		}catch(Exception e1) {
		//			e1.printStackTrace();
		//		}
    	//	}
    	//});
    	//tempMenu.add(item2);
    	
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
	
	//layerList�� �ִ� BufferImage���� ���� �̸��� layerName�迭�� �ʱ�ȭ
	public void initArrayFromList(DefaultListModel<String> layerName2, ArrayList<Layer> layerList2) {
		layerName.clear();
		for (int i = 0; i < layerList2.size(); i++) {
			layerName.insertElementAt(layerList2.get(i).getName(), i);
		}
	}
	
	//���̾ ����ִ� ������ ��ġ�� �Ӽ� �ʱ�ȭ
	public void initLayer() {
		//JButton ����------------------------------------
		JButton jb_addLayer = new JButton("add");
		jb_addLayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
                DTO dto=new DTO();
                addLayer(null);
				dto.setLayerBoolean(true);
				dto.setCommand(Info.LAYER);
				//System.out.println("layerlist "+ layerList.size());
				
				try {
					writer.writeObject(st.encrypt(dto));
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		JButton jb_delLayer = new JButton("del");
		jb_delLayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(jl.isSelectionEmpty() == false) {
					DTO dto=new DTO();
	                delLayer(selected_layerindex);
					dto.setCommand(Info.LAYER);
					dto.setLayerBoolean(false);
					dto.setL(selected_layerindex);
					System.out.println("layerlist "+ layerList.size());
	                 
					try {
						writer.writeObject(st.encrypt(dto));
					}catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		jb_addLayer.setBounds(440,170,60,30);
		jb_delLayer.setBounds(510,170,60,30);
		jb_addLayer.setFocusable(false);
		jb_delLayer.setFocusable(false);
		
		f.add(jb_addLayer);
		f.add(jb_delLayer);
		
		
		//JList ����---------------------------------------
		layerList = new ArrayList<Layer>();
		layerName = new DefaultListModel<String>();
		
		//JList �ֽ�ȭ
		//��, ������ Jlist�� ǥ��Ǵ� ���� layerName�迭�̴�.
		//initeArrayFromList(layerName, layerList);
		
		//����� jl�� �⺻ �Ӽ����� ���� ���� ������.
		jl = new JList<String>(layerName);
		jl.setFocusable(false);
		jl.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (jl.getSelectedIndex() != -1) { 
					selected_layerindex = jl.getSelectedIndex();
					g = layerList.get(selected_layerindex).getGraphics();
					//System.out.println("you select number of " + selected_layerindex);
				}
			}
		});
		jl.setSelectedIndex(0);
		jl.setFocusable(false);
		
		JScrollPane s = new JScrollPane(jl);
		s.setBounds(440,200,130,220);
		f.add(s);
	}

	//���̾ �߰�, ����, ���Ѻ���, �켱��������, �̸��ٲٱ�
	public void addLayer(Layer temp) {
		if(temp == null) {
			Layer l = new Layer("layer" + layerList.size());
			l.setPriority(layerList.size());
			Graphics2D g2d = (Graphics2D) l.getGraphics();
			g2d.setComposite(AlphaComposite.Clear);
			layerName.insertElementAt("layer" + layerList.size(), layerList.size());
			layerList.add(l);
		}
		else {
			layerName.insertElementAt("layer" + layerList.size(), layerList.size());
			layerList.add(temp);
			updateCanvas();
		}
	}
	public void delLayer(int index) {
		layerList.remove(index);
		layerName.remove(layerList.size());
		for(int i = index; i < layerList.size(); i++ ) {
			layerList.get(i).setPriority(i);
		}
		updateCanvas();
		
		//���õ� layer�� �����ǰ��� �ٽ� �׸��� �׸��� ���ؼ��� ���̾ �缱���؝� ��.
		jl.clearSelection();
	}
	
	public void updateCanvas() {
		Graphics _g = bi.getGraphics();
		_g.clearRect(0, 0, 400, 400);
		_g.setColor(Color.WHITE);
		_g.fillRect(0, 0, 400, 400);
		for(Layer cho:layerList) {
			bi.getGraphics().drawImage(cho, 0, 0, 400,400, null);
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		canvas.repaint();
	}
	
	public void initCanvas() {
		 	bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
		 
	        //BufferedImage: �̹��� �ȼ� ������ ���ۿ� ������ �̹����� ó���ϴ� Ŭ����
	        g=bi.getGraphics();
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
	
	public void initFetch() {
		DTO dto=new DTO();
        dto.setCommand(Info.FETCH);
		try {
			writer.writeObject(st.encrypt(dto));
		}catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void setCanvas() {
		f=new JFrame("Canvas");
		f.setSize(600,500);
        f.setLocationRelativeTo(null);
        f.setLayout(null);
        f.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		//EXIT(1): canvas���� â�� ���� �ڵ鷯�� EXIT1 ����
    			DTO dto=new DTO();
    			dto.setNickname(nickname);
    			dto.setCommand(Info.EXIT1);
    			try {
    				writer.writeObject(st.encrypt(dto));
    				System.out.println("������ EXIT1 ����");
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    			
    		}
        });
        
        
       
        initMenu();
        
        initSlider();
        initLayer();
        initCanvas();
        initRadioButton();
        initFetch();
        
        // color picker
        colorPicker=new myColorPicker(Color.BLACK);
        
        //�귯�� ����
    	Brush bb=new Brush(col, 20);
        bb.setBounds(20,20,400,400);
        bb.setCol(colorPicker.getCol());
        
        chatBoard=new myChatBoard(writer,roomID,nickname,userCnt);

        
        
        canvas.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
            	
            	if(jl.isSelectionEmpty() == false) {
	                //�귯�ø� ȭ�鿡 �ǽð� ����
            		bb.set_isMouseDrag(true);
            		bb.setX(bb.getXx());
            		bb.setY(bb.getYy());
	            	 bb.setXx(e.getX());
	                 bb.setYy(e.getY());
	                 
	                 if(brushMode == BrushMode.DRAW) {
	                	Graphics2D g2d = (Graphics2D) g;
	          			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
	          			g2d.setComposite(ac);             	 
	                 }
	                 else if(brushMode == BrushMode.ERASE) {
	         			Graphics2D g2d = (Graphics2D) g;
	         			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
	         			g2d.setComposite(ac);
	                 }
	
	      			 bb.paint(g);
	                 updateCanvas();
	                 
	                 //�귯�ø� dto�� �־ ����
	                 //�ʼ� �׸� 4����
	                 DTO dto=new DTO();
	                 
					dto.setCommand(Info.DRAW);
					dto.setL(selected_layerindex);
					dto.setBrushMode(brushMode);
					dto.setB(st.encrypt(bb));
	                 
					try {
						writer.writeObject(st.encrypt(dto));
					}catch(Exception e1) {
						e1.printStackTrace();
					}
            	}
            }
            
            public void mouseMoved(MouseEvent e) {

            }           
        }); 
        
        canvas.addMouseListener(new MouseAdapter() {
            
        	
        	public void mousePressed(MouseEvent e) {
        		
        		if(jl.isSelectionEmpty() == false) {
	        		//select Layer
        			bb.set_isMouseDrag(false);
	        		bb.setXx(e.getX());
	        		bb.setYy(e.getY());
	            	bb.setDia(diaCol.getValue());
	    			bb.setCol(colorPicker.getCol());
	        		
	        		//set color
	        		if(brushMode == BrushMode.DRAW) {
	        			Graphics2D g2d = (Graphics2D) g;
	          			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
	          			g2d.setComposite(ac);
	        		}
	        		else if(brushMode == BrushMode.ERASE) {
	        			Graphics2D g2d = (Graphics2D) g;
	        			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
	        			g2d.setComposite(ac);
	        		}
	        		bb.paint(g);
	    			updateCanvas();
	
	            	// ���ο� line�� �Էµȴٴ� info ����
	        		//�ʼ� �׸� 4����
	                DTO dto=new DTO();
		            dto.setCommand(Info.LINE_START);
		            dto.setB(st.encrypt(bb));
		            dto.setL(selected_layerindex);
		            dto.setBrushMode(brushMode);
	                
					try {
						writer.writeObject(st.encrypt(dto));
					}catch(Exception e1) {
						e1.printStackTrace();
					}
        		}
            	
        	}
        	// ���ο� line �Է��� �Ϸ�Ǿ��ٴ� info ����
        	public void mouseReleased(MouseEvent e) {
        		if(jl.isSelectionEmpty() == false) {
	        		DTO dto=new DTO();
	                dto.setCommand(Info.LINE_FINISH);
					try {
						writer.writeObject(st.encrypt(dto));
					}catch(Exception e1) {
						e1.printStackTrace();
					}
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
						
						String base64Member = (String) reader.readObject();
						DTO dto = (DTO) st.decrypt(base64Member);
						
						if(dto.getCommand()==Info.ROOMLIST) {
							System.out.println("we got roomLIST!");
							System.out.println(dto.getRoomList());
						}
						else if(dto.getCommand()==Info.SEND) {
							chatBoard.readData(dto);
						}
						else if (dto.getCommand() == Info.LAYER) {
							//System.out.println("recvLayer!!!");
							boolean isAddLayer = dto.getLayerBoolean();
							int deleteLayerIndex = dto.getL();
							
							if(isAddLayer == true) {
								//if(dto.getl() == null)
								int LayerSize = dto.getLsize();
								if(LayerSize == -1)
									addLayer(null);
								else {
									ArrayList<BufferedImage> temp1 = new ArrayList<BufferedImage>();
									for(int i = 0; i < LayerSize; i++) {
										BufferedImage ol = ImageIO.read(reader);
										temp1.add(ol);
									}
									for(int i = 0; i < LayerSize; i++) {
										//BufferedImage ol = temp1.get(i);
										String tt = (String)reader.readObject();
										
										BufferedImage _l = st.decrypt_layer(tt);
										Layer l = new Layer("test");
										Graphics2D g2d = (Graphics2D) l.getGraphics();
										g2d.setComposite(AlphaComposite.Clear);
										Graphics __g = l.getGraphics();
										__g.drawImage(_l, 0, 0, 400, 400, null);
										l.setPriority(i);
										

										addLayer(l);
									}
								}
								//else
								//	addLayer((Layer)st.decrypt(dto.getl()));
							}
							else if(isAddLayer == false){
								delLayer(deleteLayerIndex);
							}
						}
						else if(dto.getCommand()==Info.CREATE) {
							chatBoard.create(dto);
						}
						else if(dto.getCommand()==Info.ENTER) {
							chatBoard.enter(dto);
						}
						//EXIT(3): Ŭ���̾�Ʈ���Լ� EXIT1�� ������ EXIT2 �������ϰ� ������ ���� 
						else if (dto.getCommand() == Info.EXIT1) {
							System.out.println("receive EXIT1 from handler");
							DTO sendDTO=new DTO();
							sendDTO.setCommand(Info.EXIT2);
							sendDTO.setNickname(nickname);
							try {
								writer.writeObject(st.encrypt(sendDTO));
							}catch(IOException e) {
								e.printStackTrace();
							}
							System.out.println("send EXIT2 to server");
							chatBoard.subUserCnt();
							break;
						}
						// EXIT(3): ���۹��� EXIT3�� ���� � Ŭ���̾�Ʈ�� ������ �����ߴ��� �ľ�
						else if (dto.getCommand() == Info.EXIT3) {
							System.out.println("receive EXIT4 from "+dto.getNickname());
							chatBoard.exit1(dto);
						}
						else if (dto.getCommand() == Info.DRAW || 
								dto.getCommand() == Info.LINE_START){
							//dto.getB().print();
							
							//dto�� �ִ� �귯�� ��ü�� ����
							//�ʼ� �׸� 3����
							//1. �귯�� ���� = Brush
							//2. ���õ� ���̾� ���� = int
							//3. �귯���� ��� = BrushMode
							Brush recvbb= (Brush) st.decrypt(dto.getB()); //recvbb = [B@3e2f815f
							int recvLayerPriority = dto.getL();
							BrushMode recvBrushMode = dto.getBrushMode();
							recvbb.setBounds(20,20,400,400);
							
							Graphics recvG = layerList.get(recvLayerPriority).getGraphics();
							
							if(recvBrushMode == BrushMode.DRAW) {
			                	Graphics2D g2d = (Graphics2D) recvG;
			          			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
			          			g2d.setComposite(ac);             	 
			                 }
			                 else if(recvBrushMode == BrushMode.ERASE) {
			         			Graphics2D g2d = (Graphics2D) recvG;
			         			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
			         			g2d.setComposite(ac);
			                 }
							
						     //�ǽð� ������ �귯�ø� ���������� �ǽð� ����
							recvbb.paint(recvG);
						    updateCanvas();
						    
						}
						else if (dto.getCommand() == Info.FETCH) {
							//���� FETCH�� ������ handlerList�� index 0���� FETCH dto�� ������
							//�� ��, �ش� dto�� ���� Ŭ��(index 0�� Ŭ��)�� �ڽ��� ������ �ִ� Layer���� ���񿡰� ������.

							DTO sendDTO = new DTO();
							sendDTO.setCommand(Info.FETCH2);
							sendDTO.setLsize(layerList.size());
							try {
								writer.writeObject(st.encrypt(sendDTO));
							}catch(Exception e1) {
								e1.printStackTrace();
							}
							
							for(Layer l:layerList) {
								writer.writeObject(st.encrypt_layer((BufferedImage)l));
							}
							
						}
					}catch(IOException e) {
						e.printStackTrace();
					}catch(ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);
				
				try {
					reader.close();
					writer.close();
					socket.close();
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		recvThread.setDaemon(true);
		recvThread.start();
	}
	

	/*
	
	 */
	public paintClient() {
		
		try {
			socket=new Socket(serverIP,port);
			writer=new ObjectOutputStream(socket.getOutputStream());
			reader=new ObjectInputStream(socket.getInputStream());
			st = new serialTransform();
			//Entryreader=new ObjectInputStream(socket.getInputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
		entry=new myEntry(writer, reader, socket);
		
		while(entry.currentStat() == 0) {
			System.out.print("");
		}
		System.out.println("currentStat is "+entry.currentStat());
		if(entry.currentStat() == 1) { 
			nickname=entry.getNickname();
			roomID=entry.getRoomID();
			userCnt=entry.getUserCnt();
			
			setCanvas();
			
			recvData();
		}
		else if(entry.currentStat()==2) {
			DTO dto=new DTO();
			dto.setCommand(Info.EXIT3);
			System.out.println("send EXIT3");
			try {
				writer.writeObject(st.encrypt(dto));
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			while(socket.isClosed()) {
				break;
			}
			try {
				reader.close();
				writer.close();
				socket.close();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new paintClient();
	}
}