

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.Scanner;

public class Client {
	private Socket clientSocket;	// Ŭ���̾�Ʈ ����
	private DataInputStream dataInputStream; // �����͸� ��� �����ϴ� ������
	private DataOutputStream dataOutputStream; // �����͸� ��� �����ϴ� ������
	private int PORT = 9790;
	private String IP = "127.0.0.1";
	
	public void setting() {
		
		try {
			// 1. connect
			System.out.println("connecting to server...");
			clientSocket = new Socket(IP,PORT);
			System.out.println("connected to server");
			
			// stream setting
			dataInputStream=new DataInputStream(clientSocket.getInputStream());
			dataOutputStream=new DataOutputStream(clientSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
	}
	public void closeAll() {
		try {
			clientSocket.close();
			dataInputStream.close();
			dataOutputStream.close();
			System.out.println("socket and stream are closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dataRecv() {
		try {
			String data=dataInputStream.readUTF();
			System.out.println("[Clinet] message from server: "+data);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		Scanner in=new Scanner(System.in);
		try {
			System.out.println("[client] : ");
			String data=in.nextLine();
			dataOutputStream.writeUTF(data);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Client() {
		setting();
		sendData();
		closeAll();
	}
	
	public static void main(String[] args) {
		new Client();
	}

}
