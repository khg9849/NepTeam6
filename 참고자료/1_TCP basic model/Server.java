import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	private ServerSocket serverSocket;	// 서버소켓
	private Socket clientSocket;		// 클라이언트소켓
	private DataInputStream dataInputStream; 	//데이터를 계속 수신하는 스레드
	private DataOutputStream dataOutputStream; 	//데이터를 계속 전송하는 스레드
	private final int PORT = 9790;
	
	public void setting() {
		try {
			// 1. bind
			serverSocket=new ServerSocket(PORT);
			System.out.println("["+PORT+"] server is made");
			// 2. accept
			clientSocket=serverSocket.accept();
			System.out.println("client is connected");
			
			// stream setting
			dataInputStream=new DataInputStream(clientSocket.getInputStream());
			dataOutputStream=new DataOutputStream(clientSocket.getOutputStream());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeAll() {
		try {
			dataInputStream.close();
			dataOutputStream.close();
			clientSocket.close();
			serverSocket.close();
			System.out.println("socket and stream are closed");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void recvData() {
		try {
			String data=dataInputStream.readUTF();
			System.out.println("message from client: "+data);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendData() {
		Scanner in=new Scanner(System.in);
		try {
			String data=in.nextLine();
			dataOutputStream.writeUTF(data);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Server() {
		setting();
		recvData();
		//sendData();
		closeAll();
	}
	
	
	public static void main(String[] args) {
		new Server();
	}

}
