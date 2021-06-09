

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
	private String CreateError = new String("�̹� �����ϴ� ���Դϴ�.");
	private String JoinError = new String("���� �������� �ʽ��ϴ�.");
	
	//�� ����� �޴�
	JFrame CMenu = new JFrame("�� �����");
	JFrame JMenu = new JFrame("�� ����");
	JFrame QJMenu;
	JFrame Error = new JFrame("����");
	JButton ErrorBtn = new JButton("Ȯ��");
	JButton Create = new JButton("Ȯ��");
	JButton Join = new JButton("����");
	JButton QJoin = new JButton("����");
	JButton Cancel = new JButton("���");
	JLabel IDLabel = new JLabel("�� �̸�");
	JLabel PWLabel = new JLabel("�н�����");
	JLabel nicknameLabel = new JLabel("�г���");
	JLabel ErrorMsg = new JLabel();
	JTextField RID = new JTextField();
	JPasswordField RPW = new JPasswordField();
	
	
	JTextField Nname = new JTextField();
	
	private String Originridlist;//�ʱ� �� id����
	private String Originrpwlist;//�ʱ� �� pw����
	
	
	public String getRoomID() {
		return roomID;
	}
	public String getNickname() {
		return nickname;
	}
	
	public String[] makeList(){//string �迭�� roomlist�� ��ū���� �ɰ��� ����
		
		String[] rlist = new String[10]; // �� �ִ� ����Ʈ ���� : 100
		String temp;
		int count = 0;
		StringTokenizer strkRoom;
		
		temp = Originridlist;
		strkRoom = new StringTokenizer(temp, "/");// /������ ��ū �ɰ�����
		
		while(strkRoom.hasMoreTokens()){ //��ū�� �������������
			rlist[count++] = strkRoom.nextToken();//����Ʈ�� �� �̸� �߰�
		}
			
		
		
		return rlist;
	}
	
	public void setRoomIDPWList() {//id pw�� �̸� �޾ƿͼ� ����
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
	
	public boolean ValidationRoomPW(String rid, String rpw) {//��й�ȣ üũ�ϱ�
		String rlist;
		String pwlist;
		StringTokenizer strkRoom;//��ū����
		StringTokenizer strkPw;
		int count = 0;
		
		rlist = Originridlist;
		pwlist = Originrpwlist;
		strkRoom = new StringTokenizer(rlist, "/");// /������ ��ū �ɰ�����
		strkPw = new StringTokenizer(pwlist, "/");
		
		while(strkRoom.hasMoreTokens()){ //��ū�� �������������
			count++;
			if(strkRoom.nextToken().equals(rid)) { // ������ū�̶� ������ ����!
				break;
			}
			System.out.println(count);
		}
		
		if(strkRoom.countTokens() == 0) { // ���� ��ū�� �ϳ��� ������ ����Ʈ�� ���̸��� ���ٴ°� �ǹ��մϴ�
			//System.out.println("no room");
			JoinError = "�������� �ʴ� ���Դϴ�.";
			return false;
		}
		else {
			for(int i = 0; i < count-1; i++) {//������ū�� ������ ī��Ʈ��ŭ ������ū���� ����
				System.out.println(strkPw.nextToken());
			}
			if(strkPw.nextToken().equals(rpw)) {//�ش���ū ��й�ȣ�� ���ٸ�
				return true;//true�� ��ȯ
			}
		}
			
		
		JoinError = "��й�ȣ�� �ٸ��ϴ�.";
		return false;
	}
	
	
	public boolean ValidationRoomID(String rid) { //�� �ߺ��˻�
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
		// roomID, roomPW, nickName �ޱ�
		
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
		// roomID, roomPW, nickName �ޱ�
		
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
		// roomID, roomPW, nickName �ޱ�
		QJMenu = new JFrame(roomid + " ����");
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
				JoinError = "������ ���� �����ϴ�.";
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
				JoinError = "���� ������ �ּ���";
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
