import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class chatHandler extends Thread {
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	private List<chatHandler>list;
	
	public chatHandler(Socket socket, List<chatHandler>list) {
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
		chatDTO dto=null;
		String nickName=null;
		
		try {
			while(true) {
				dto=(chatDTO)reader.readObject();
				System.out.println("message from "+dto.getNickName()+
						" is received: "+dto.getCommand());
				
				if(dto.getCommand()==Info.JOIN) {
					nickName=dto.getNickName();
					chatDTO sendDTO=new chatDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(nickName+" is entered");
					broadcast(sendDTO);
				}
				
				else if(dto.getCommand()==Info.EXIT) {
					chatDTO sendDTO=new chatDTO();
					sendDTO.setCommand(Info.EXIT);
					writer.writeObject(sendDTO);
					writer.flush();
					
					reader.close();
					writer.close();
					socket.close();
					list.remove(this);
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(nickName+"¥‘ ≈¿Â«œºÃΩ¿¥œ¥Ÿ");
					
					broadcast(sendDTO);
					break;
				}
				
				else if(dto.getCommand()==Info.SEND){
					chatDTO sendDTO=new chatDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setNickName(dto.getNickName());
					String msg=dto.getMessage();
					nickName=dto.getNickName();
					sendDTO.setMessage("["+nickName+"]: "+msg);
					broadcast(sendDTO);
					System.out.println("broadcast is finished");
					
				}
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void broadcast(chatDTO dto) {
		for(chatHandler cho:list) {
			try {
				cho.writer.writeObject(dto);
				cho.writer.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
