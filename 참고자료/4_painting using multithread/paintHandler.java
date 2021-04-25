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
				//Ŭ���̾�Ʈ���� ���� dto�� ���� �ȿ� �ִ� Handler���� ����
				paintDTO dto = (paintDTO)reader.readObject();
				
				//Ŭ���̾�Ʈ ���� �޼����� ��� dto�� Handler ����
				if(dto.getCommand()==Info.EXIT) {
					reader.close();
					writer.close();
					socket.close();
					list.remove(this);
					System.out.println("exit client");
					break;
				}
				else if(dto.getCommand()==Info.DRAW) {
					
					//Ŭ���̾�Ʈ���� ���� dto�� ���Ͽ� ����� ��� Ŭ���̾�Ʈ�鿡�� ����
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
