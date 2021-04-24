import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class paintServer{
	private ServerSocket serverSocket;
	private ArrayList<paintHandler> list;
	private final int port=9790;
	
	public paintServer() {
		   try {
	            serverSocket = new ServerSocket(port);
	            System.out.println("["+port+"] server is made");
	            list = new ArrayList<paintHandler>();
	            while (true) {
	                Socket socket = serverSocket.accept();
	                paintHandler handler = new paintHandler(socket, list);
	                handler.start();
	                list.add(handler);
	            }
	        } catch (Exception e) {
	        }
	}

	public static void main(String[] args) {
		new paintServer();

	}

}
