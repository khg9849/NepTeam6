

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
	
private Scanner in = new Scanner(System.in);
	
	public void getInfo() {
		// roomID, roomPW, nickName ¹Þ±â
		System.out.println("Enter roomID: ");
		roomID=in.next();
		System.out.println("Enter roomPW: ");
		roomPW=in.next();
		System.out.println("Enter nickname: ");
		nickname=in.next();
	}
	
	class createBttnClicked implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			getInfo();
			
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
		}
		
	}
	class joinBttnClicked implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			getInfo();
			
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
