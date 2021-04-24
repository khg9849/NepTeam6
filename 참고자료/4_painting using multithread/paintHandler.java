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
		this.socket=socket;
		this.list=list;
		try {
			writer=new ObjectOutputStream(socket.getOutputStream());
			reader=new ObjectInputStream(socket.getInputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		paintDTO dto=null;
		try {
			while(true) {
				dto=(paintDTO)reader.readObject();
				if(dto.getCommand()==Info.EXIT) {
					reader.close();
					writer.close();
					socket.close();
					list.remove(this);
					break;
				}
				else if(dto.getCommand()==Info.DRAW) {
					System.out.println("read data from client...");
					dto.getB().print();
					
					paintDTO sendDTO=new paintDTO();
					sendDTO.setB(dto.getB());
					broadcast(sendDTO);
					
					sendDTO.getB().print();
					System.out.println("broadcast is done");
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
