

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class myEntry extends JFrame {
	private Socket socket;
	private ObjectOutputStream writer;
	private JButton createBttn;
	private JButton joinBttn;
	
	private Room room;
	private String roomID;
	private String roomPW;
	private String nickname;
	//방 만들기 메뉴
	JFrame CMenu = new JFrame("방 만들기");
	JFrame JMenu = new JFrame("방 입장");
	JButton Create = new JButton("확인");
	JButton Cancel = new JButton("취소");
	JLabel IDLabel = new JLabel("방 이름");
	JLabel PWLabel = new JLabel("패스워드");
	JLabel nicknameLabel = new JLabel("닉네임");
	JTextField RID = new JTextField();
	JTextField RPW = new JTextField();
	JTextField Nname = new JTextField();
private Scanner in = new Scanner(System.in);
	
	public void getInfoCreate() {
		// roomID, roomPW, nickName 받기
		
		CMenu.setSize(400,300);
		CMenu.add(Create);
		CMenu.add(Cancel);
		CMenu.add(RID);
		CMenu.add(RPW);
		CMenu.add(Nname);
		CMenu.add(IDLabel);
		CMenu.add(PWLabel);
		CMenu.add(nicknameLabel);
		IDLabel.setBounds(50,50,75,25);
		PWLabel.setBounds(50,100,75,25);
		nicknameLabel.setBounds(50,150,75,25);
		Create.setBounds(75, 200, 75, 25);
		Cancel.setBounds(175,200,75,25);
		RID.setBounds(150, 50, 75, 25);
		RPW.setBounds(150, 100, 75, 25);
		Nname.setBounds(150, 150, 75, 25);
		CMenu.setLayout(null);
		CMenu.setVisible(true);
		Create.addActionListener(new createClick());
		Cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CMenu.dispose();
			}
		});
	}
	public void getInfoJoin() {
		// roomID, roomPW, nickName 받기
		
		JMenu.setSize(400,300);
		JMenu.add(Create);
		JMenu.add(Cancel);
		JMenu.add(RID);
		JMenu.add(RPW);
		JMenu.add(Nname);
		JMenu.add(IDLabel);
		JMenu.add(PWLabel);
		JMenu.add(nicknameLabel);
		IDLabel.setBounds(50,50,75,25);
		PWLabel.setBounds(50,100,75,25);
		nicknameLabel.setBounds(50,150,75,25);
		Create.setBounds(75, 200, 75, 25);
		Cancel.setBounds(175,200,75,25);
		RID.setBounds(150, 50, 75, 25);
		RPW.setBounds(150, 100, 75, 25);
		Nname.setBounds(150, 150, 75, 25);
		JMenu.setLayout(null);
		JMenu.setVisible(true);
		Create.addActionListener(new joinClick());
		Cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JMenu.dispose();
			}
		});
	}
	class createClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("Enter roomID: ");
			roomID=RID.getText();
			//System.out.println("Enter roomPW: ");
			roomPW=RPW.getText();
			//System.out.println("Enter nickname: ");
			nickname=Nname.getText();
			
			paintDTO dto=new paintDTO();
	        dto.setCommand(Info.CREATE);
	        dto.setRoomID(roomID);
	        dto.setRoomPW(roomPW);
	        dto.setNickname(nickname);
	        
			try {
				writer.writeObject(dto);
				writer.flush();
				writer.reset();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			
			
			CMenu.dispose();
			dispose();
		}
	}
	class joinClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("Enter roomID: ");
			roomID=RID.getText();
			//System.out.println("Enter roomPW: ");
			roomPW=RPW.getText();
			//System.out.println("Enter nickname: ");
			nickname=Nname.getText();
			
			paintDTO dto=new paintDTO();
	        dto.setCommand(Info.ENTER);
	        dto.setRoomID(roomID);
	        dto.setRoomPW(roomPW);
	        dto.setNickname(nickname);
	        
			try {
				writer.writeObject(dto);
				writer.flush();
				writer.reset();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			JMenu.dispose();
			dispose();
		}
	}
	class createBttnClicked implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			getInfoCreate();
			
		}
		
	}
	
	
	class joinBttnClicked implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			getInfoJoin();
			
		}
	}
	
	
	
	public void disappear() {
		this.setVisible(false);
	}
	public void appear() {
		this.setVisible(true);
	}
	
	public myEntry(ObjectOutputStream writer) {

		this.writer=writer;
		
		this.setTitle("Entry");
		this.setSize(600,500);
	    this.setLayout(new FlowLayout());
	   // this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
    			disappear();
        	}
        });
	    
	    createBttn=new JButton("create");
	    joinBttn=new JButton("join");
	    
	    
	    createBttn.addActionListener(new createBttnClicked());
	    joinBttn.addActionListener(new joinBttnClicked());
	    
	    
	    this.add(createBttn);
	    this.add(joinBttn);
	    this.setVisible(true);
	    
		
	}
	
	
	
}
