

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class myUserListFrame extends JFrame{
	private JTextArea userListArea;
	//private ArrayList<String> userList;
	
	myUserListFrame(ArrayList<String> userList){
		this.setTitle("user list");
		this.setSize(100,200);
		//this.userList=userList;
		// 위치 설정

        this.setLayout(null);
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
    			setVisible(false);
        	}
        });
        
        userListArea=new JTextArea();
        userListArea.setBounds(10,10,80,180);
        userListArea.setEditable(false); 
        update();
        this.add(userListArea);
        setVisible(false);
        
	}
	public void update() {
		userListArea.setText("null");
		//room 인스턴스 전송 안되면 이 기능은 버립시다
		/*
		userListArea.setText("");
		
		for(String user:userList) {
			userListArea.append(user+"\n");
		}
		*/
	}

}
public class myChatBoard extends JFrame {
	private Socket socket;
	private ObjectOutputStream writer;
	private String nickname;
	private String roomID;
	private int userCnt;
	private ArrayList<String> userList;
	
	private JPanel chatPanel;
	private JTextField textField;
	private JButton sendBttn;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton userListBttn;
	private	myUserListFrame userlistFrame;
	
	public void sendData() {
		String data=textField.getText();
		textArea.append("[Me]: "+data+"\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		
		paintDTO dto=new paintDTO();
		dto.setNickname(nickname);
		dto.setCommand(Info.SEND);
		dto.setMessage(data);
		try {
			writer.writeObject(dto);
			writer.flush();
			System.out.println("서버에 전송: "+dto.getCommand());
			textField.setText("");
		}catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void readData(paintDTO dto) {
		System.out.println("서버에서 받았습니다: "+dto.getCommand());
		textArea.append(dto.getMessage()+"\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		System.out.println(dto.getMessage()+"\n");
	}	
	
	public void create(paintDTO dto) {
		userListBttn.setText(roomID);
		textArea.append("[System]: "+dto.getRoomID()+" is created\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		enter(dto);
	}
	public void enter(paintDTO dto) {
		userCnt=dto.getUserCnt();
		userListBttn.setText(roomID+"("+userCnt+")");
		userList.add(dto.getNickname());
		textArea.append("[System]: "+dto.getNickname()+" is entered\n");
		System.out.println("userCnt is "+userCnt);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	
	public void exit1(paintDTO dto) {
		textArea.append("[System]: "+dto.getNickname()+" is exited\n");
		userCnt--;
		userListBttn.setText(roomID+"("+userCnt+")");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	
	public void initChatPanel() {
		chatPanel=new JPanel();
		textField=new JTextField();
        sendBttn=new JButton("SEND");
        textArea=new JTextArea();
        textArea.setEditable(false); 
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userListBttn=new JButton("");
        
        chatPanel.setBounds(0,0,300,500);
        scrollPane.setBounds(12, 50, 263, 340);
        textField.setBounds(12, 397, 170, 56);
        sendBttn.setBounds(193, 395, 82, 58);
        userListBttn.setBounds(12,17,92,23);
        
        chatPanel.setLayout(null);
        chatPanel.add(textField);
        chatPanel.add(sendBttn);
        chatPanel.add(scrollPane);
        chatPanel.add(userListBttn);
        
        //chatPanel.setVisible(true);
        textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if(keycode==KeyEvent.VK_ENTER)
					sendData();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        sendBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData();
			}
		});
        userListBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				userlistFrame.update();
				userlistFrame.setVisible(true);
			}
		});
	}
	
	public void disappear() {
		this.setVisible(false);
	}
	public void appear() {
		this.setVisible(true);
	}
	
	
	public myChatBoard(ObjectOutputStream writer, String roomID, String nickname, int userCnt) {
		this.roomID=roomID;
		this.nickname=nickname;
		this.userCnt=userCnt;
		
		this.setTitle("chat board");
		this.writer=writer;
		this.setSize(300,500);
		
		Dimension frameSize = this.getSize(); // frame size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // monitor size
		// 좌측에 위치
		this.setLocation(10, (screenSize.height - screenSize.height));

        this.setLayout(null);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
    			disappear();
        	}
        });
        
        userList=new ArrayList<String>();
        this.userlistFrame=new myUserListFrame(userList);
        
        initChatPanel();
		this.add(chatPanel);
		appear();
	}

	
	
}
