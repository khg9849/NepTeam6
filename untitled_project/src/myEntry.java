

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
import java.io.ObjectInputStream;
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
import java.util.StringTokenizer;


public class myEntry extends JFrame{
	//private Socket socket;
	
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private JButton createBttn;
	private JButton joinBttn;
	
	private Room room;
	private String roomID;
	private String roomPW;
	private String nickname;
	
	private boolean Cstat = false;

	
	private String CreateError = new String("이미 존재하는 방입니다.");
	private String JoinError = new String("방이 존재하지 않습니다.");
	//방 만들기 메뉴
	JFrame CMenu = new JFrame("방 만들기");
	JFrame JMenu = new JFrame("방 입장");
	JFrame Error = new JFrame("오류");
	JButton ErrorBtn = new JButton("확인");
	JButton Create = new JButton("확인");
	JButton Cancel = new JButton("취소");
	JLabel IDLabel = new JLabel("방 이름");
	JLabel PWLabel = new JLabel("패스워드");
	JLabel nicknameLabel = new JLabel("닉네임");
	JLabel ErrorMsg = new JLabel();
	JTextField RID = new JTextField();
	JTextField RPW = new JTextField();
	JTextField Nname = new JTextField();
	
	public boolean ValidationRoomPW(String rid, String rpw) {//비밀번호 체크하기
		String rlist;
		String pwlist;
		StringTokenizer strkRoom;//토큰생성
		StringTokenizer strkPw;
		int count = 0;
		try {
			paintDTO checkdto = (paintDTO)reader.readObject();
			rlist = checkdto.getRoomList();
			pwlist = checkdto.getRoomPwList();
			strkRoom = new StringTokenizer(rlist, "/");// /단위로 토큰 쪼개버려
			strkPw = new StringTokenizer(pwlist, "/");
			
			while(strkRoom.hasMoreTokens()){ //토큰이 비어있을때까지
				count++;
				if(strkRoom.nextToken().equals(rid)) { // 다음토큰이랑 같으면 멈춰!
					break;
				}
				System.out.println(count);
			}
			if(strkRoom.countTokens() == 0) { // 남은 토큰이 하나도 없으면 리스트에 방이름이 없다는걸 의미합니다
				System.out.println("no room");
				return false;
			}
			else {
				for(int i = 0; i < count; i++) {//남은토큰이 있으면 카운트만큼 다음토큰으로 전진
					System.out.println(strkPw.nextToken());
				}
				if(strkPw.nextToken().equals(rpw)) {//해당토큰 비밀번호랑 같다면
					return true;//true를 반환
				}
			}
			
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("pw error");
		return false;
	}
	
	
	public boolean ValidationRoomID(String rid) { //방 중복검사
		String rlist;
		
		try {
			paintDTO checkdto = (paintDTO)reader.readObject();
			rlist = checkdto.getRoomList();
			if(!rlist.isEmpty()) {	
				if(rlist.contains(rid)) {
					return true;
				}
			}
			return false;
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e1) {
			e1.printStackTrace();
		}

		return false;
	}
	public boolean currentStat() {
		return this.Cstat;
	}
	public void CreateError() {
		Error.setSize(300,200);
		Error.add(ErrorBtn);
		Error.add(ErrorMsg);
		ErrorMsg.setText(CreateError);
		ErrorBtn.setBounds(100,100,100,50);
		ErrorMsg.setBounds(50,50,200,50);
		Error.setLayout(null);
		Error.setVisible(true);
		
		ErrorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Error.dispose();
			}
		});
		
	}
	public void JoinError() {
		Error.setSize(300,200);
		Error.add(ErrorBtn);
		Error.add(ErrorMsg);
		ErrorMsg.setText(JoinError);
		ErrorBtn.setBounds(100,100,100,50);
		ErrorMsg.setBounds(50,50,200,50);
		Error.setLayout(null);
		Error.setVisible(true);
		
		ErrorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Error.dispose();
			}
		});
		
	}
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
		RID.setText("");
		RPW.setText("");
		Nname.setText("");
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
		RID.setText("");
		RPW.setText("");
		Nname.setText("");
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
		
			roomID=RID.getText();
			roomPW=RPW.getText();
			nickname=Nname.getText();

			paintDTO dto=new paintDTO();

			dto.setCommand(Info.ROOMLIST);
			try {
				writer.writeObject(dto);
				writer.flush();
				writer.reset();
			}catch(Exception e1) {
				e1.printStackTrace();
			}
			//System.out.println(ValidationRoomID(roomID));
			if(!ValidationRoomID(roomID)) {
				System.out.println("create room");
				
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
				Cstat = true;

				CMenu.dispose();
				dispose();
			}
			else {
				CreateError();
				CMenu.dispose();
			}
			
		}
			

		
	}
	class joinClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {

			roomID=RID.getText();
			roomPW=RPW.getText();
			nickname=Nname.getText();
			
			paintDTO dto=new paintDTO();
			
			
			dto.setCommand(Info.ROOMLIST);
			try {
				writer.writeObject(dto);
				writer.flush();
				writer.reset();
			}catch(Exception e1) {
				e1.printStackTrace();
			}

			if(ValidationRoomPW(roomID, roomPW)) {
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
				Cstat = true;
				JMenu.dispose();
				dispose();
			}
			else {
				
				JoinError();
				JMenu.dispose();
			}
			
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
	
	public myEntry(ObjectOutputStream writer, ObjectInputStream reader) {

		this.writer=writer;
		this.reader=reader;
	
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
