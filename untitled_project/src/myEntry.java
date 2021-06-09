

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.StringTokenizer;


public class myEntry extends JFrame{
	
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private myIO mio;
	
	private JButton createBttn;
	private JButton joinBttn;
	private JButton QuickjoinBttn;
	private JList roomJList;
	
	//private Room room;
	private String roomID;
	private String roomPW;
	private String nickname;
	private String QjoinID = "";
	private int Cstat = 0;
	private int userCnt;
	private serialTransform st;
	
	private String[] roomList;
	private String CreateError = new String("이미 존재하는 방입니다.");
	private String JoinError = new String("방이 존재하지 않습니다.");
	
	//방 만들기 메뉴
	JFrame CMenu = new JFrame("방 만들기");
	JFrame JMenu = new JFrame("방 입장");
	JFrame QJMenu;
	JFrame Error = new JFrame("오류");
	JButton ErrorBtn = new JButton("확인");
	JButton Create = new JButton("확인");
	JButton Join = new JButton("입장");
	JButton QJoin = new JButton("입장");
	JButton Cancel = new JButton("취소");
	JLabel IDLabel = new JLabel("방 이름");
	JLabel PWLabel = new JLabel("패스워드");
	JLabel nicknameLabel = new JLabel("닉네임");
	JLabel ErrorMsg = new JLabel();
	JTextField RID = new JTextField();
	JPasswordField RPW = new JPasswordField();
	
	
	JTextField Nname = new JTextField();
	
	private String Originridlist;//초기 방 id정보
	private String Originrpwlist;//초기 방 pw정보
	
	
	public String getRoomID() {
		return roomID;
	}
	public String getNickname() {
		return nickname;
	}
	
	public String[] makeList(){//string 배열에 roomlist를 토큰으로 쪼개서 저장
		
		String[] rlist = new String[10]; // 방 최대 리스트 개수 : 100
		String temp;
		int count = 0;
		StringTokenizer strkRoom;
		
		temp = Originridlist;
		strkRoom = new StringTokenizer(temp, "/");// /단위로 토큰 쪼개버려
		
		while(strkRoom.hasMoreTokens()){ //토큰이 비어있을때까지
			rlist[count++] = strkRoom.nextToken();//리스트에 방 이름 추가
		}
			
		
		
		return rlist;
	}
	
	public void setRoomIDPWList() {//id pw를 미리 받아와서 세팅
		DTO _dto = new DTO();
		_dto.setCommand(Info.ROOMLIST);
		try {
			//writer.writeObject(st.encrypt(_dto));
			mio.myWrite(_dto);
		}catch(Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			//String recvDTO = (String) this.reader.readObject();
			//DTO dto = (DTO) st.decrypt(recvDTO);
			DTO dto = mio.myRead();
			
			
			Originridlist = dto.getRoomList();
			Originrpwlist = dto.getRoomPwList();
			
		}/*catch(ClassNotFoundException e) {
			e.printStackTrace();
		}*/catch(IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean ValidationRoomPW(String rid, String rpw) {//비밀번호 체크하기
		String rlist;
		String pwlist;
		StringTokenizer strkRoom;//토큰생성
		StringTokenizer strkPw;
		int count = 0;
		
		rlist = Originridlist;
		pwlist = Originrpwlist;
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
			//System.out.println("no room");
			JoinError = "존재하지 않는 방입니다.";
			return false;
		}
		else {
			for(int i = 0; i < count-1; i++) {//남은토큰이 있으면 카운트만큼 다음토큰으로 전진
				System.out.println(strkPw.nextToken());
			}
			if(strkPw.nextToken().equals(rpw)) {//해당토큰 비밀번호랑 같다면
				return true;//true를 반환
			}
		}
			
		
		JoinError = "비밀번호가 다릅니다.";
		return false;
	}
	
	
	public boolean ValidationRoomID(String rid) { //방 중복검사
		String rlist;
		
		rlist = Originridlist;
		if(!rlist.isEmpty()) {	
			if(rlist.contains(rid)) {
				return true;
			}
		}
		return false;
		
	}
	public int currentStat() {
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
	public void getInfoJoin(String roomid) {
		// roomID, roomPW, nickName 받기
		
		JMenu.setSize(400,300);
		JMenu.add(Join);
		JMenu.add(Cancel);
		
		JMenu.add(RPW);
		JMenu.add(Nname);
		
		JMenu.add(PWLabel);
		JMenu.add(nicknameLabel);
		
		PWLabel.setBounds(50,100,75,25);
		nicknameLabel.setBounds(50,150,75,25);
		Join.setBounds(75, 200, 75, 25);
		Cancel.setBounds(175,200,75,25);
		if(roomid.isEmpty()) {
			IDLabel.setBounds(50,50,75,25);
			JMenu.add(RID);
			JMenu.add(IDLabel);
			RID.setText("");
			RID.setBounds(150, 50, 75, 25);
		}
		else {
			RID.setText(roomid);
		}
		RPW.setText("");
		Nname.setText("");
		
		RPW.setBounds(150, 100, 75, 25);
		Nname.setBounds(150, 150, 75, 25);
		JMenu.setLayout(null);
		JMenu.setVisible(true);
		Join.addActionListener(new joinClick());
		Cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JMenu.dispose();
			}
		});
	}
	
	public void getInfoQJoin(String roomid) {
		// roomID, roomPW, nickName 받기
		QJMenu = new JFrame(roomid + " 입장");
		QJMenu.setSize(400,300);
		QJMenu.add(QJoin);
		QJMenu.add(Cancel);
		
		QJMenu.add(RPW);
		QJMenu.add(Nname);
		
		QJMenu.add(PWLabel);
		QJMenu.add(nicknameLabel);
		
		PWLabel.setBounds(50,100,75,25);
		nicknameLabel.setBounds(50,150,75,25);
		QJoin.setBounds(75, 200, 75, 25);
		Cancel.setBounds(175,200,75,25);
		
		
		RPW.setText("");
		Nname.setText("");
		
		RPW.setBounds(150, 100, 75, 25);
		Nname.setBounds(150, 150, 75, 25);
		QJMenu.setLayout(null);
		QJMenu.setVisible(true);
		QJoin.addActionListener(new QjoinClick());
		Cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				QJMenu.dispose();
			}
		});
	}
	
	class createClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			char[] secret_pw = RPW.getPassword();
			roomPW = "";
			for(char c: secret_pw) {
				Character.toString(c);
				roomPW+= (roomPW.equals("")) ? "" + c + "" : "" + c + "";
			}
			//roomPW=RPW.getText();
			roomID=RID.getText();
			nickname=Nname.getText();

			DTO dto=new DTO();

			dto.setCommand(Info.ROOMLIST);
			try {
				//writer.writeObject(st.encrypt(dto));
				mio.myWrite(dto);
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
					//writer.writeObject(st.encrypt(dto));
					mio.myWrite(dto);
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				Cstat = 1;

				CMenu.dispose();
				dispose();
			}
			else {
				CreateError();
				CMenu.dispose();
			}
			
		}
			

		
	}
	class QjoinClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			char[] secret_pw = RPW.getPassword();
			roomPW = "";
			for(char c: secret_pw) {
				Character.toString(c);
				roomPW+= (roomPW.equals("")) ? "" + c + "" : "" + c + "";
			}
			roomID=QjoinID;
			//roomPW=RPW.getText();
			nickname=Nname.getText();
			
			DTO dto=new DTO();
			
			
			dto.setCommand(Info.ROOMLIST);
			try {
				//writer.writeObject(st.encrypt(dto));
				mio.myWrite(dto);
			}catch(Exception e1) {
				e1.printStackTrace();
			}

			if(ValidationRoomPW(roomID, roomPW)) {
		        dto.setCommand(Info.ENTER);
		        dto.setRoomID(roomID);
		        dto.setRoomPW(roomPW);
		        dto.setNickname(nickname);
		        
				try {
					//writer.writeObject(st.encrypt(dto));
					mio.myWrite(dto);
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				Cstat = 1;
				QJMenu.dispose();
				dispose();
			}
			else {
				
				JoinError();
				QJMenu.dispose();
			}
			
		}
	}
	
	
	
	
	class joinClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			char[] secret_pw = RPW.getPassword();
			roomPW = "";
			for(char c: secret_pw) {
				Character.toString(c);
				roomPW+= (roomPW.equals("")) ? "" + c + "" : "" + c + "";
			}
			roomID=RID.getText();
			//roomPW=RPW.getText();
			nickname=Nname.getText();
			
			DTO dto=new DTO();
			
			
			dto.setCommand(Info.ROOMLIST);
			try {
				//writer.writeObject(st.encrypt(dto));
				mio.myWrite(dto);
			}catch(Exception e1) {
				e1.printStackTrace();
			}

			if(ValidationRoomPW(roomID, roomPW)) {
		        dto.setCommand(Info.ENTER);
		        dto.setRoomID(roomID);
		        dto.setRoomPW(roomPW);
		        dto.setNickname(nickname);
		        
				try {
					//writer.writeObject(st.encrypt(dto));
					mio.myWrite(dto);
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				Cstat = 1;
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
			if(Originridlist.length() == 2) {
				JoinError = "생성된 방이 없습니다.";
				System.out.println(Originridlist);
				JoinError();
			}
			else
				getInfoJoin("");
			
		}
	}
	
	class QjoinBttnClicked implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(roomJList.isSelectionEmpty()) {
				JoinError = "방을 선택해 주세요";
				System.out.println(Originridlist);
				JoinError();
			}
			else if(QjoinID != null && !QjoinID.equals("") && !QjoinID.equals("\n"))
				getInfoQJoin(QjoinID);
			
		}
	}
	
	public void disappear() {
		this.setVisible(false);
	}
	public void appear() {
		this.setVisible(true);
	}
	
	public myEntry(ObjectOutputStream writer, ObjectInputStream reader, Socket socket) {

		this.socket=socket;
		this.writer=writer;
		this.reader=reader;
		st = new serialTransform();
		mio = new myIO(this.writer, this.reader);
		
		RPW.setEchoChar('*');
		this.setTitle("Open Canvas");
		this.setSize(600,500);
	    this.setLayout(new FlowLayout());
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
    			disappear();
    			Cstat = 2;
    			System.exit(EXIT_ON_CLOSE);
 //   			exit(1);
        	}
        });
	    
	    setRoomIDPWList();
	    roomList = makeList();
	    
	    roomJList = new JList(roomList);
	    roomJList.setBounds(142,100,300,200);
	    createBttn=new JButton("create");
	    joinBttn=new JButton("join");
	    QuickjoinBttn = new JButton("Qjoin");
	    createBttn.setBounds(107,400,100,50);
	    joinBttn.setBounds(257,400,100,50);
	    QuickjoinBttn.setBounds(407,400,100,50);
	    createBttn.addActionListener(new createBttnClicked());
	    joinBttn.addActionListener(new joinBttnClicked());
	    QuickjoinBttn.addActionListener(new QjoinBttnClicked());
	    
	    roomJList.addListSelectionListener(new ListSelectionListener() {
	    	
	    	public void valueChanged(ListSelectionEvent e) {
	    		if(!e.getValueIsAdjusting()) {
	    			QjoinID = (String) roomJList.getSelectedValue();
	    			System.out.println(QjoinID);
	    		}
	    	}
	    	
	    });
	    
	    this.add(roomJList);
	    this.add(QuickjoinBttn);
	    this.add(createBttn);
	    this.add(joinBttn);
	    this.setLayout(null);
	    this.setVisible(true);
	    
		
	}
	public int getUserCnt() {
		return userCnt;
	}
	
	
	
}
