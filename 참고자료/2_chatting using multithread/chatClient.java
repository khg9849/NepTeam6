

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class chatClient {
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private final String serverIP = "127.0.0.1";
	private final int port = 9790;
	
	private String nickName;
	
	Scanner in=new Scanner(System.in);
	
	public void recvData() {
		Thread recvThread=new Thread(new Runnable() {

			@Override
			public void run() {
				chatDTO dto = null;
				while (true) {
					try {
						dto = (chatDTO) reader.readObject();
						System.out.println("�������� �޾ҽ��ϴ�: "+dto.getCommand());
						if (dto.getCommand() == Info.EXIT) {
							reader.close();
							writer.close();
							socket.close();
							System.out.println(nickName + "�� EXIT�� �����Ͽ� Ŭ���̾�Ʈ�� �����մϴ�");
							break;
						} else if (dto.getCommand() == Info.SEND) {
							System.out.println(dto.getMessage()+"\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
		});
		recvThread.setDaemon(true);
		recvThread.start();
	}
	
	public void sendData() {
		
		
		new Thread(new Runnable() {
			boolean isThread=true;
			@Override
			public void run() {
				
				
				
				while(isThread){
					
					System.out.println("["+nickName+"] : ");
					String data=in.nextLine();
					chatDTO dto=new chatDTO();
					dto.setNickName(nickName);
					
					if(data.equals("quit")) {
						dto.setCommand(Info.EXIT);
						isThread=false;
					}
					else {
						dto.setCommand(Info.SEND);
						dto.setMessage(data);
					}
					try {
						writer.writeObject(dto);
						writer.flush();
						System.out.println("������ ����: "+dto.getCommand());
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	public chatClient() {
		
		// ���� ����
		try {
			socket=new Socket(serverIP,port);
			writer=new ObjectOutputStream(socket.getOutputStream());
			reader=new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//�г��� ����
		System.out.println("Set your nickname: ");
		nickName=in.nextLine();
		
		// send nickName
		chatDTO dto=new chatDTO();
		dto.setCommand(Info.JOIN);
		dto.setNickName(nickName);
		try {
			writer.writeObject(dto);
			writer.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		sendData();
		recvData();
		
	}
	
	public static void main(String[] args) {
		new chatClient();

	}
}
