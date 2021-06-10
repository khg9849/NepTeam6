

import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class myChatBoard extends JFrame {
	private Socket socket;
	private ObjectOutputStream writer;
	private serialTransform st;
	private myIO mio;
	
	private String nickname;
	private String roomID;
	
	private JPanel chatPanel;
	private JTextField textField;
	private JButton sendBttn;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton userListBttn;
	
	public void sendData() {
		String data=textField.getText();
		textArea.append("[Me]: "+data+"\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		
		DTO dto=new DTO();
		dto.setNickname(nickname);
		dto.setCommand(Info.SEND);
		dto.setMessage(data);
		try {
			mio.myWrite(dto);
			System.out.println("서버에 전송: "+dto.getCommand());
			textField.setText("");
		}catch(Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void readData(DTO dto) {
		System.out.println("서버에서 받았습니다: "+dto.getCommand());
		textArea.append(dto.getMessage()+"\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		System.out.println(dto.getMessage()+"\n");
	}	
	
	public void create(DTO dto) {
		userListBttn.setText(roomID);
		textArea.append("[System]: "+dto.getRoomID()+" is created\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		enter(dto);
	}
	public void enter(DTO dto) {
		userListBttn.setText(roomID);
		textArea.append("[System]: "+dto.getNickname()+" entered\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	
	public void exit1(DTO dto) {
		textArea.append("[System]: "+dto.getNickname()+" exited\n");
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
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if(keycode==KeyEvent.VK_ENTER)
					sendData();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}
        	
        });
        sendBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData();
			}
		});
       
	}
	
	public void disappear() {
		this.setVisible(false);
	}
	public void appear() {
		this.setVisible(true);
	}
	
	
	public myChatBoard(ObjectOutputStream writer, String roomID, String nickname) {
		this.roomID=roomID;
		this.nickname=nickname;
		
		this.setTitle("chat board");
		this.writer=writer;
		st = new serialTransform();
		mio = new myIO(this.writer, null);
		
		this.setSize(300,500);
		
		Dimension frameSize = this.getSize(); // frame size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // monitor size 좌측에 위치
		this.setLocation(10, (screenSize.height - screenSize.height));

        this.setLayout(null);
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
    			disappear();
        	}
        });
        
        
        initChatPanel();
		this.add(chatPanel);
	}

	
	
}
