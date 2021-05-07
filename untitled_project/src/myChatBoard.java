

import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class myChatBoard extends JFrame {
	private Socket socket;
	private ObjectOutputStream writer;
	private String nickname;
	
	public String getNickname() {
		return nickname;
	}


	private JPanel startPanel;
	private JTextField nicknameField;
	private JButton nicknameBttn;
	
	private JPanel chatPanel;
	private JTextField textField;
	private JButton bttn;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	


	public void setNickname() {
		nickname=nicknameField.getText();
		paintDTO dto=new paintDTO();
		
		dto.setCommand(Info.JOIN);
		dto.setNickname(nickname);
		try {
			writer.writeObject(dto);
			writer.flush();
		}catch(IOException e1) {
			e1.printStackTrace();
		}
		startPanel.setVisible(false);
		chatPanel.setVisible(true);
	}
	
	public void sendData() {
		String data=textField.getText();
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
	
	
	public void initStartPanel() {
		startPanel=new JPanel();
        nicknameField=new JTextField("Set your nickname");
        nicknameBttn=new JButton("SUBMIT");
        
        startPanel.setBounds(0,0,600,500);
        nicknameField.setBounds(100, 100, 150, 80);
        nicknameBttn.setBounds(270, 100, 100, 80);
        
        startPanel.setLayout(null);
        startPanel.add(nicknameField);
        startPanel.add(nicknameBttn);
        
        nicknameField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if(keycode==KeyEvent.VK_ENTER)
					setNickname();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        nicknameBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNickname();
			}
        });
	
        nicknameField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nicknameField.setText("");
			}
        	
        });
	}
	
	
	public void initChatPanel() {
		chatPanel=new JPanel();
		textField=new JTextField(10);
        bttn=new JButton("SEND");
        textArea=new JTextArea();
        textArea.setEditable(false); 
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        chatPanel.setBounds(0,0,600,500);
        scrollPane.setBounds(10, 10, 500, 300);
        textField.setBounds(10, 350, 500, 30);
        bttn.setBounds(450, 330, 80, 30);
        
        chatPanel.setLayout(null);
        chatPanel.add(textField);
        chatPanel.add(bttn);
        chatPanel.add(scrollPane);
        
        chatPanel.setVisible(false);
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
        bttn.addActionListener(new ActionListener() {

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
	
	
	public myChatBoard(ObjectOutputStream writer) {
		this.setTitle("chat board");
		this.writer=writer;
		this.setSize(600,500);
		
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
        
        initStartPanel();
        initChatPanel();
        
		this.add(startPanel);
		this.add(chatPanel);
        
		appear();
	}
	
}
