import java.awt.Dimension;
import java.awt.Label;
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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class initialMenu extends JFrame {
	
	JFrame Menu = new JFrame("Open Canvas");
	JFrame NewRoom = new JFrame("�� �����");
	JFrame SearchRoom = new JFrame("�� ã��");
	JButton findRoom = new JButton("�� ã��");
	JButton makeRoom = new JButton("�� �����");
	JButton makeRoomAccept = new JButton("Ȯ��");
	JButton searchRoomAccept = new JButton("����");
	JButton Cancel = new JButton("���");
	JTextField roomID = new JTextField(10);
	JTextField roomPW = new JTextField(10);
	JTextField MaxUser = new JTextField(10);
	Label rid = new Label("�� �̸� ");
	Label rpw= new Label("�н����� ");
	Label rsize= new Label("�ο� ");
	
	ArrayList<roomStat> roomList = new ArrayList<roomStat>();
	int count = 0;
	public void makeMenu() {
	
		findRoom.setBounds(240,150,100,50);
		makeRoom.setBounds(240,250,100,50);
		makeRoom.addActionListener(new makeRoom());
		findRoom.addActionListener(new searchRoom());
		
		Menu.add(findRoom);
		Menu.add(makeRoom);
		
		Menu.setSize(600,500);
		Menu.setLayout(null);
		Menu.setVisible(true);
		
	}
	
	public initialMenu() {
		makeMenu();
	}
	public void SearchRoom() {
		SearchRoom.setSize(400,300);
		SearchRoom.add(roomID);
		SearchRoom.add(roomPW);
		SearchRoom.add(rid);
		SearchRoom.add(rpw);
		SearchRoom.add(searchRoomAccept);
		SearchRoom.add(Cancel);
		rid.setBounds(50,75,50,25);
		rpw.setBounds(50,100,50,25);
	
		roomID.setBounds(150, 75, 150, 25);
		roomPW.setBounds(150, 100, 150, 25);
		makeRoomAccept.setBounds(75,200,100,50);
		makeRoomAccept.addActionListener(new newRoomAccept());
		Cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SearchRoom.dispose();
			}
			
		});
		Cancel.setBounds(200,200,100,50);
	
		SearchRoom.setLayout(null);
		SearchRoom.setVisible(true);
	}
	
	public void NewRoom() {
		NewRoom.setSize(400,300);
		NewRoom.add(roomID);
		NewRoom.add(roomPW);
		NewRoom.add(MaxUser);
		NewRoom.add(rid);
		NewRoom.add(rpw);
		NewRoom.add(rsize);
		NewRoom.add(makeRoomAccept);
		NewRoom.add(Cancel);
		rid.setBounds(50,75,50,25);
		rpw.setBounds(50,100,50,25);
		rsize.setBounds(50,125,50,25);
		roomID.setBounds(150, 75, 150, 25);
		roomPW.setBounds(150, 100, 150, 25);
		makeRoomAccept.setBounds(75,200,100,50);
		makeRoomAccept.addActionListener(new newRoomAccept());
		Cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NewRoom.dispose();
			}
			
		});
		Cancel.setBounds(200,200,100,50);
		MaxUser.setBounds(150, 125, 150, 25);
		NewRoom.setLayout(null);
		NewRoom.setVisible(true);
	}
	
	public static void main(String argv[]) {
		new initialMenu();
	}
	
	class makeRoom implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			NewRoom();
			
		}
	}
	class searchRoom implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			SearchRoom();
			
		}	
	}
	
	class searchRoomAccept implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			roomStat R = new roomStat();
			R.setroomName(roomID.getText());
			R.setPW(roomPW.getText());
			
			int key = -1;
			if(count == 0) {
				System.out.println("���� �������� �ʽ��ϴ�.");
			}
			else {
				
				if(roomList.contains(R)) {
					key = 1;
				}
				
				if(key == -1) {
					//�� ã�� �� ����
					System.out.println("���� �������� �ʽ��ϴ�.");

				}
				else {
					//�� ã��
					System.out.println("�� ����");
					//�� ����
					
					NewRoom.dispose();
				}
			}
		}
	}
	
	class Cancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dispose();
		}
	}
	class newRoomAccept implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			roomStat R = new roomStat();
			R.setroomName(roomID.getText());
			R.setPW(roomPW.getText());
			R.setMaxUser(8);
			int key = -1;
			if(count == 0) {
				roomList.add(R);
				System.out.println("�� ����");
				count++;
				NewRoom.dispose();
			}
			else {
				
				if(roomList.contains(R)) {
					key = 1;
				}
				
				if(key == -1) {
					//���ο� �� ����
					roomList.add(R);
					System.out.println(count + " " + "�� ����");
					count++;
					//�� ���� �� �ٷ� ����
					
					NewRoom.dispose();
				}
				else {
					//�̹� �ִ� ��
					System.out.println("�̹� �����ϴ� ��");
					NewRoom.dispose();
				}
			}
			
		}
		
	}
	class findRoom implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}


