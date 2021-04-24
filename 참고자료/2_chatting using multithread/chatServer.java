import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class chatServer {

	private ServerSocket serverSocket;
    private List<chatHandler> list;
    private final int port=9790;
    
	public chatServer() {
		   try {
	            serverSocket = new ServerSocket(port);
	            System.out.println("["+port+"] server is made");
	            list = new ArrayList<chatHandler>();
	            while (true) {
	                Socket socket = serverSocket.accept();
	                chatHandler handler = new chatHandler(socket, list);
	                handler.start();
	                list.add(handler);
	            }
	        } catch (Exception e) {
	        }
	}

	public static void main(String[] args) {
		new chatServer();

	}

}
