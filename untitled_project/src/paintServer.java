import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class paintServer{
	
	private ServerSocket serverSocket;
	private ArrayList<Room> roomList;
	private final int port=9790;
	
	
	public paintServer() {
		
		   try {
	            serverSocket = new ServerSocket(port);
	            System.out.println("["+port+"] server is ready to get client");
	            roomList = new ArrayList<Room>();
	            while (true) {
	                Socket socket = serverSocket.accept();
	                System.out.println("connection is made");
	                
	                paintHandler handler = new paintHandler(socket, roomList);
	                handler.start();
	            }
	        } catch (Exception e) {
	        }
	}

	public static void main(String[] args) {
		new paintServer();

	}

}
