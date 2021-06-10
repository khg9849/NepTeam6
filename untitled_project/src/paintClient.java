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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

//현재 유저가 컨트롤하는 Brush의 역할
enum BrushMode{
	DRAW, ERASE
}

public class paintClient{

	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private final String serverIP="127.0.0.1";	//127.0.0.1
	private final int port=9790;
	private serialTransform st;
	private myIO mio;
	
	// client 고유 번호
	private int ClientUID;
	public int getClientUID() {return ClientUID;}
	public void setClientUID(int n) {ClientUID = n;}
	
	private String roomID;
	private String nickname;
	
	// 캔버스 관련 오브젝트
	private JFrame f;
	private JLabel canvas;
	private BufferedImage bi;
	private Graphics g;
	private Color col;
	
	private myEntry entry;	// 시작화면
	private myColorPicker colorPicker;	// 색상선택창
	private myChatBoard chatBoard;		// 채팅창
	private JMenuBar mb;	//메뉴바
	
	//현재 브러시의 역할 (브러시, 지우개, 레이어 선택)
	private BrushMode brushMode;
	private JRadioButton rb_draw;
	private JRadioButton rb_erase;
	private JSlider diaCol;	// 직경 슬라이더
	
	private boolean isThread=true;
	
	public boolean isRecvDataStart = true;
	private ArrayList<Layer> layerList;	//layer의 정보 리스트
	private DefaultListModel<String> layerName;	//layer의 이름 리스트
	JList<String> jl;
	private int addLayer_initcount = 0;
	private int selected_layerindex = 0;
	
	public void initDrawingTool() {
		/*______________ 직경 조절 슬라이더 _________________ */
		diaCol = new JSlider(JSlider.HORIZONTAL, 1, 50, 20);
		diaCol.setBounds(450,20, 100,30);
		diaCol.setFocusable(false);
        f.add(diaCol);
        
        /*______________ Draw & Erase 선택 버튼 ____________ */
        
      		rb_draw = new JRadioButton("draw (B)");
      		rb_draw.addItemListener(new ItemListener() {
      			public void itemStateChanged(ItemEvent e) {
      				brushMode = BrushMode.DRAW;
      			}
      		});
      		rb_erase = new JRadioButton("erase (E)");
      		rb_erase.addItemListener(new ItemListener() {
      			public void itemStateChanged(ItemEvent e) {
      				brushMode = BrushMode.ERASE;
      			}
      		});
      		
      		//초기 선택은 draw모드
      		rb_draw.setSelected(true);
      		rb_draw.setFocusable(false);
      		rb_erase.setFocusable(false);
      		
      		//라디오 버튼 그룹화 = 여러 옵션들 중에 하나만 선택하게 만들기
      		ButtonGroup b_group = new ButtonGroup();
      		b_group.add(rb_draw);
      		b_group.add(rb_erase);
      		
  
      		rb_draw.setBounds(450,50, 150,30);
      		rb_erase.setBounds(450,80, 150,30);
            f.add(rb_draw);
            f.add(rb_erase);
	}
	
	public void initMenu() {
        mb=new JMenuBar();
    	JMenu showMenu=new JMenu("Show");
    	JMenu fileMenu=new JMenu("File");
    	mb.add(showMenu);
    	mb.add(fileMenu);
    	
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
    	
    	
    	JMenuItem item3=new JMenuItem("Save");
    	item3.addActionListener(new ActionListener() {
    		 public void actionPerformed(ActionEvent e) {
    			 
                 try { 
                	 String fileName=JOptionPane.showInputDialog("저장할 파일 이름을 입력하세요");
        			 ImageIO.write(bi, "PNG", new File("./src/"+fileName+".PNG")); 
        			 int result=JOptionPane.showConfirmDialog(item3, "파일이 저장되었습니다. 폴더를 여시겠습니까?","Confirm",JOptionPane.YES_NO_OPTION);
        			 if(result==JOptionPane.YES_OPTION)
        				 Desktop.getDesktop().open(new File("./src"));	 
                 }
                 	
                 catch (IOException e1) { e1.printStackTrace(); }               
              }            
          });
    	fileMenu.add(item3);
    	
    	f.setJMenuBar(mb);
    	 
	}
	/*
	//layerList에 있는 BufferImage들의 고유 이름을 layerName배열로 초기화
	public void initArrayFromList(DefaultListModel<String> layerName2, ArrayList<Layer> layerList2) {
		layerName.clear();
		for (int i = 0; i < layerList2.size(); i++) {
			layerName.insertElementAt(layerList2.get(i).getName(), i);
		}
	}
	*/
	
	//레이어가 들어있는 상자의 위치와 속성 초기화
	public void initLayer() {
		//JButton 생성------------------------------------
		JButton jb_addLayer = new JButton("add");
		jb_addLayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
                DTO dto=new DTO();
                addLayer(null);
				dto.setLayerBoolean(true);
				dto.setCommand(Info.LAYER);
				try {
					mio.myWrite(dto);
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
						mio.myWrite(dto);
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
		
		
		//JList 생성---------------------------------------
		layerList = new ArrayList<Layer>();
		layerName = new DefaultListModel<String>();
		
		//JList 최신화
		//즉, 실제로 Jlist에 표기되는 것은 layerName배열이다.
		//initeArrayFromList(layerName, layerList);
		
		//참고로 jl의 기본 속성값은 다중 구간 선택임.
		jl = new JList<String>(layerName);
		jl.setFocusable(false);
		jl.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (jl.getSelectedIndex() != -1) { 
					selected_layerindex = jl.getSelectedIndex();
					g = layerList.get(selected_layerindex).getGraphics();
				}
			}
		});
		jl.setSelectedIndex(0);
		jl.setFocusable(false);
		
		JScrollPane s = new JScrollPane(jl);
		s.setBounds(440,200,130,220);
		f.add(s);
	}

	//레이어를 추가, 삭제, 권한변경, 우선순위변경, 이름바꾸기
	public void addLayer(Layer temp) {
		// add layer
		if(temp == null) {
			Layer l = new Layer("layer" + layerList.size());
			l.setPriority(layerList.size());
			Graphics2D g2d = (Graphics2D) l.getGraphics();
			g2d.setComposite(AlphaComposite.Clear);
			layerName.insertElementAt("layer" + layerList.size(), layerList.size());
			layerList.add(l);
		}
		// fetch layer
		else {
			// 파라미터로 받은 레이어 정보를 layerLit에 추가한다
			layerName.insertElementAt("layer" + layerList.size(), layerList.size());
			layerList.add(temp);
			updateCanvas();
		}
	}
	public void delLayer(int index) {
		// index는 삭제할 layer의 index
		layerList.remove(index);
		layerName.remove(layerList.size());
		for(int i = index; i < layerList.size(); i++ ) {
			layerList.get(i).setPriority(i);
		}
		updateCanvas();
		
		// 선택된 layer를 삭제하고 다음 레이어가 자동으로 선택되는 것을 막음
		jl.clearSelection();
	}
	
	public void updateCanvas() {
		//Canvas의 버퍼이미지에서 그래픽부분을 가져온다.
		Graphics _g = bi.getGraphics();
		
		//Canvas의 배경을 흰색으로 설정한다.
		_g.clearRect(0, 0, 400, 400);
		_g.setColor(Color.WHITE);
		_g.fillRect(0, 0, 400, 400);
		
		// layerList의 하위 레이어부터 버퍼이미지에 그린다.
		for(Layer cho:layerList) {
			bi.getGraphics().drawImage(cho, 0, 0, 400,400, null);
		}
		
		//빠른 프레임 전환으로 인해 생기는 깜빡임 현상(Flickering)을 최소화하기 위해
		//이전에 호출한 updateCanvas와 지금 호출한 updateCanvas의 sleep 간격을 정한다.
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//버퍼이미지를 Canvas로 올린다.
		canvas.repaint();
	}
	
	public void initCanvas() {
		 	bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB );
		 
	        //BufferedImage: 이미지 픽셀 정보를 버퍼에 저장해 이미지를 처리하는 클래스
	        g=bi.getGraphics();
	        g.setColor(Color.WHITE);
	        g.fillRect(0, 0, 400, 400);
	        canvas=new JLabel(new ImageIcon(bi));
	        canvas.setBounds(20,20,400,400);
	        f.add(canvas);
	        
	        // Fetch
	        DTO dto=new DTO();
	        dto.setCommand(Info.FETCH);
			try {
				mio.myWrite(dto);
			}catch(Exception e1) {
				e1.printStackTrace();
			}
	}

	
	public void start() {
		f=new JFrame("Canvas");
		f.setSize(600,500);
        f.setLocationRelativeTo(null);
        f.setLayout(null);
        f.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		//EXIT(1): canvas에서 창을 끄면 핸들러에 EXIT1 전송
    			DTO dto=new DTO();
    			dto.setNickname(nickname);
    			dto.setCommand(Info.EXIT1);
    			try {
    				mio.myWrite(dto);
    				System.out.println("서버에 EXIT1 전송");
    			}catch(Exception e1) {
    				e1.printStackTrace();
    			}
    			
    		}
        });
        
        initMenu();
        initDrawingTool();
        initLayer();
        initCanvas();
        
        colorPicker=new myColorPicker(Color.BLACK);	 // 색상선택창
        chatBoard=new myChatBoard(writer,roomID,nickname); //채팅창
        
        //브러시 설정
    	Brush bb=new Brush(col, 20);
        bb.setBounds(20,20,400,400);
        bb.setCol(colorPicker.getCol());
        
        
        canvas.addMouseMotionListener( new MouseMotionListener() {
            
            public void mouseDragged(MouseEvent e) {
            	
            	if(jl.isSelectionEmpty() == false) {
	                //브러시를 화면에 실시간 구현
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
	                 
	                 //브러시를 dto에 넣어서 보냄
	                 //필수 항목 4가지
	                 DTO dto=new DTO();
	                 
					dto.setCommand(Info.DRAW);
					dto.setL(selected_layerindex);
					dto.setBrushMode(brushMode);
					dto.setB(st.encrypt(bb));
	                 
					try {
						mio.myWrite(dto);
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
	
	            	// 새로운 line이 입력된다는 info 전송
	        		//필수 항목 4가지
	                DTO dto=new DTO();
		            dto.setCommand(Info.LINE_START);
		            dto.setB(st.encrypt(bb));
		            dto.setL(selected_layerindex);
		            dto.setBrushMode(brushMode);
	                
					try {
						mio.myWrite(dto);
					}catch(Exception e1) {
						e1.printStackTrace();
					}
        		}
            	
        	}
        	// 새로운 line 입력이 완료되었다는 info 전송
        	public void mouseReleased(MouseEvent e) {
        		if(jl.isSelectionEmpty() == false) {
	        		DTO dto=new DTO();
	                dto.setCommand(Info.LINE_FINISH);
					try {
						mio.myWrite(dto);
					}catch(Exception e1) {
						e1.printStackTrace();
					}
        		}
        	}

        });
        //키 이벤트 생성
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
        //키 이벤트는 포커스를 받을 수 있는 컴포넌트를 설정해 주어야 한다.
        canvas.setFocusable(true);
        
        f.setVisible( true );
	}
	
	public void recvData() {
		Thread recvThread=new Thread(new Runnable() {

			@Override
			public void run() {
				
				while(isThread) {
					try {
						DTO dto = mio.myRead();
						if(dto == null) {
							System.out.println("DTO is null");
							continue;
						}
						
						if(dto.getCommand()==Info.ROOMLIST) {
							System.out.println("we got roomLIST:");
							System.out.println(dto.getRoomList());
						}
						else if(dto.getCommand()==Info.SEND) {
							chatBoard.readData(dto);
						}
						else if (dto.getCommand() == Info.LAYER) {
							boolean isAddLayer = dto.getLayerBoolean();
							int deleteLayerIndex = dto.getL();
							
							if(isAddLayer == true) {
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
										// 뉴비는 최신 레이어 정보를 반복문으로 받는다.
										String tt = (String)reader.readObject();
										
										BufferedImage _l = st.decrypt_layer(tt);
										Layer l = new Layer("test");
										Graphics2D g2d = (Graphics2D) l.getGraphics();
										g2d.setComposite(AlphaComposite.Clear);
										Graphics __g = l.getGraphics();
										__g.drawImage(_l, 0, 0, 400, 400, null);
										l.setPriority(i);
										
										// 파라미터로 null이 아닌 값을 보낸다.	
										addLayer(l);
									}
								}
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
						//EXIT(3): 클라이언트에게서 EXIT1을 받으면 EXIT2 재전송하고 스레드 종료 
						else if (dto.getCommand() == Info.EXIT1) {
							System.out.println("receive EXIT1 from handler");
							DTO sendDTO=new DTO();
							sendDTO.setCommand(Info.EXIT2);
							sendDTO.setNickname(nickname);
							try {
								mio.myWrite(sendDTO);
							}catch(IOException e) {
								e.printStackTrace();
							}
							System.out.println("send EXIT2 to server");
							break;
						}
						// EXIT(3): 전송받은 EXIT3를 통해 어떤 클라이언트가 접속을 종료했는지 파악
						else if (dto.getCommand() == Info.EXIT3) {
							System.out.println("receive EXIT4 from "+dto.getNickname());
							chatBoard.exit1(dto);
						}
						else if (dto.getCommand() == Info.DRAW || 
								dto.getCommand() == Info.LINE_START){
							//dto.getB().print();
							
							//dto에 있는 브러시 객체를 꺼냄
							//필수 항목 3가지
							//1. 브러시 정보 = Brush
							//2. 선택된 레이어 층수 = int
							//3. 브러시의 모드 = BrushMode
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
							
						     //실시간 구현된 브러시를 마찬가지로 실시간 재현
							recvbb.paint(recvG);
						    updateCanvas();
						    
						}
						else if (dto.getCommand() == Info.FETCH) {
							//뉴비가 FETCH를 보내면 index가 0인 client에게 FETCH dto를 보낸다
							//해당 client는 자신이 가지고 있던 Layer들을 뉴비에게 보낸다.

							DTO sendDTO = new DTO();
							sendDTO.setCommand(Info.FETCH2);
							sendDTO.setLsize(layerList.size());
							try {
								mio.myWrite(sendDTO);
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
				mio.shutdown();
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
	

	public paintClient() {
		
		try {
			socket=new Socket(serverIP,port);
			writer=new ObjectOutputStream(socket.getOutputStream());
			reader=new ObjectInputStream(socket.getInputStream());
			st = new serialTransform();
			mio = new myIO(writer, reader);
			
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
			start();
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