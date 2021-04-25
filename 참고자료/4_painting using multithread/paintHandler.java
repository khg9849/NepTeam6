import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class paintHandler extends Thread {
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	private ArrayList<paintHandler>list;
		
	public paintHandler(Socket socket, ArrayList<paintHandler> list) {
		try {
			this.socket=socket;
			this.list=list;
			this.writer=new ObjectOutputStream(socket.getOutputStream());
			this.reader=new ObjectInputStream(socket.getInputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while(true) {
				//클라이언트에서 보낸 dto는 서버 안에 있는 Handler들이 받음
				paintDTO dto = (paintDTO)reader.readObject();
				
				//클라이언트 종료 메세지가 담긴 dto면 Handler 종료
				if(dto.getCommand()==Info.EXIT) {
					reader.close();
					writer.close();
					socket.close();
					list.remove(this);
					System.out.println("exit client");
					break;
				}
				else if(dto.getCommand()==Info.DRAW) {
					
					//클라이언트에서 받은 dto를 소켓에 연결된 모든 클라이언트들에게 보냄
					broadcast(dto);
					
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void broadcast(paintDTO dto) {
		for(paintHandler cho:list) {
			try {
				cho.writer.writeObject(dto);
				cho.writer.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
