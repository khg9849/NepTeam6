import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

import javax.imageio.ImageIO;

public class paintHandler extends Thread {
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	private Object mutex;
	private myIO mio;
	
	private ArrayList<Room> roomList;
	private Room room;
	private ArrayList<paintHandler>handlerList;
	
	private Line line; //�ڵ鷯�� ���� brush ����Ʈ
	private ArrayList<Line> lineList; //line ����Ʈ
	private serialTransform st;
	
	private String nickname;
	
	public paintHandler(Socket socket, ArrayList<Room> roomList, Object m) {
		try {
			this.socket=socket;
			this.roomList=roomList;
			this.mutex = m;
			this.writer=new ObjectOutputStream(socket.getOutputStream());
			this.reader=new ObjectInputStream(socket.getInputStream());
			this.lineList=new ArrayList<Line>();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("handler is ready");
	}

	public void run() {
		try {
			while(true) {
				
				//Ŭ���̾�Ʈ���� ���� dto�� ���� �ȿ� �ִ� Handler���� ����
				st = new serialTransform();
				mio = new myIO(writer, reader);
				
				DTO dto = mio.myRead();
				
				boolean cflag = true;
				if(dto.getCommand()==Info.ROOMLIST) {
					sendRoomList();
				}
				
				else if(dto.getCommand()==Info.CREATE) {
					String roomID=dto.getRoomID();
					String roomPW=dto.getRoomPW();
					this.nickname=dto.getNickname();
					// �ߺ��Ǵ� roomID Ȯ�� 
					for(Room r:roomList) {
						System.out.println("for\n");
						// 1. �ߺ��� => �޽��� ����
						if(r.getRoomID().equals(roomID)) {
							System.out.println("�� �̸��� �ߺ��ȴ�.");
							cflag = false;
						}
						
					}
				
					// 2. �ߺ� �� �� => �� ���� 
					if(cflag) {

						handlerList=new ArrayList<paintHandler>();
						room=new Room(roomID,roomPW,handlerList);
						roomList.add(room);
						System.out.println("room ["+roomID+"] is created");
						
						room.enter(this);
						System.out.println(nickname+" entered "+roomID);
						DTO sendDTO=new DTO();
						sendDTO.setCommand(Info.CREATE);
						sendDTO.setRoomID(roomID);
						sendDTO.setNickname(nickname);
						try {
							mio.myWrite(sendDTO);
						}catch(IOException e) {
							e.printStackTrace();
						}
					}
				}
				else if(dto.getCommand()==Info.ENTER) {
					String roomID=dto.getRoomID();
					String roomPW=dto.getRoomPW();
					this.nickname=dto.getNickname();
					int flag=0;
					
					// �ش� ���� �ִ��� Ȯ��
					for(Room r:roomList) {
						// 1. �� ã��
						if(r.getRoomID().equals(roomID)) {
								System.out.println("room ["+roomID+"] is found");
								this.room=r;
								room.enter(this);
								handlerList=room.getHandlerList();
								System.out.println(nickname+" entered "+roomID);
								DTO sendDTO=new DTO();
								sendDTO.setCommand(Info.ENTER);
								sendDTO.setNickname(nickname);
								broadcast(sendDTO);
								try {
									mio.myWrite(sendDTO);
								}catch(IOException e) {
									e.printStackTrace();
								}
								flag=1;
								
						}
						
					}

				}
				//EXIT(1): Ŭ���̾�Ʈ���Լ� EXIT1�� ������ �ٽ� EXIT1 ����
				else if(dto.getCommand()==Info.EXIT1) {
					System.out.println("receive EXIT1 from "+dto.getNickname());
					DTO sendDTO=new DTO();
					sendDTO.setCommand(Info.EXIT1);
					try {
						//writer.writeObject(st.encrypt(sendDTO));
						mio.myWrite(sendDTO);
					}catch(IOException e) {
						e.printStackTrace();
					}
					System.out.println("send EXIT1 to "+dto.getNickname());
				}
				//EXIT(2): Ŭ���̾�Ʈ���Լ� EXIT2�� ������ ���� ����, handlerList���� ����
				// 			�ش� Ŭ���̾�Ʈ�� ���� �����ٴ� ���� EXIT3�� ��� broadcast
				else if(dto.getCommand()==Info.EXIT2) {
					System.out.println("receive EXIT2 from "+dto.getNickname());
					synchronized (mutex) {
						reader.close();
						writer.close();
						socket.close();
					}
					room.exit1();
					handlerList.remove(this);
					
					DTO sendDTO=new DTO();
					sendDTO.setCommand(Info.EXIT3);
					sendDTO.setNickname(nickname);
					broadcast(sendDTO);
					System.out.println("broadcast EXIT3");
					break;
				}
				else if(dto.getCommand()==Info.EXIT3) {
					try {
						synchronized (mutex) {
							reader.close();
							writer.close();
							socket.close();
						}
					}
					catch(IOException e) {
						e.printStackTrace();
					}
					break;
				}
				else if(dto.getCommand()==Info.ENTER) {
					DTO sendDTO=new DTO();
					nickname=dto.getNickname();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage("["+nickname+"]  entered");
					broadcast(sendDTO);
				}
				else if(dto.getCommand()==Info.SEND) {
					DTO sendDTO=new DTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage("["+dto.getNickname()+"]: "+dto.getMessage());
					broadcast(sendDTO);
				}
				else if(dto.getCommand()==Info.DRAW) {
					//Ŭ���̾�Ʈ���� ���� dto�� ���Ͽ� ����� ��� Ŭ���̾�Ʈ�鿡�� ����
					broadcast(dto);
				}
				else if(dto.getCommand()==Info.LINE_START) {
					broadcast(dto);
				}
				else if(dto.getCommand()==Info.LINE_FINISH) {
					lineList.add(line); // line �Է� ���� -> lineList�� �߰�
					System.out.println("new line is added\n");
				}
				// FETCH�� ��û������ �ٸ� �ڵ鷯�� ������ �ִ� lineList�� �����ؼ�
				// ��û�� Ŭ���̾�Ʈ�� ����
				else if(dto.getCommand()==Info.FETCH) {
					paintHandler temp = handlerList.get(0);
					if(temp != this) {
						//room�� �ִ� paintHandler�� �迭���� index==0���� FETCH�߼�
						DTO sendDTO = new DTO();
						sendDTO.setCommand(Info.FETCH);
						try {
							temp.mio.myWrite(sendDTO);
						}catch(IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				else if (dto.getCommand() == Info.FETCH2) {
					//index 0�� �ڵ鷯�� �ش� Ŭ�� ������ �ִ� ���̾� ������ ��°�� ����
					paintHandler newbie = handlerList.get(handlerList.size() -1);
					
					DTO layerDTO = new DTO();
					layerDTO.setLayerBoolean(true);
					layerDTO.setLsize(dto.getLsize());
					layerDTO.setCommand(Info.LAYER);
					try {
						newbie.mio.myWrite(layerDTO);
					}catch(IOException e){
						e.printStackTrace();
					}

					for(int i = 0; i < dto.getLsize(); i++) {
						String tt = (String)reader.readObject();
						newbie.writer.writeObject(tt);
					}
				}
				else if(dto.getCommand() == Info.LAYER) {
					broadcast(dto);
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	private void sendRoomList() {
		String roomlist="/";
		String roompwlist = "/";
		for(Room room : roomList){
			String roomID=room.getRoomID();
			String roomPW=room.getPW();
			roomlist+=roomID+"/";
			roompwlist+=roomPW+"/";
		}
		roomlist+="\n";
		roompwlist+="\n";
		DTO dto=new DTO();
		dto.setCommand(Info.ROOMLIST);
		dto.setRoomList(roomlist);
		dto.setRoomPwList(roompwlist);
		System.out.println("we will send roomlist: "+roomlist);
		System.out.println("we will send roompwlist: "+roompwlist);
		try {
			mio.myWrite(dto);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcast(DTO dto) {
		if(handlerList.isEmpty()) return;
		for(paintHandler cho:handlerList) {
			try {
				if(cho != this) {
					cho.mio.myWrite(dto);
				}
				else {
					continue;
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
