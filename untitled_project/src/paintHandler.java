import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class paintHandler extends Thread {
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	
	private ArrayList<Room> roomList;
	private Room room;
	private ArrayList<paintHandler>handlerList;
	
	private Line line; //�ڵ鷯�� ���� brush ����Ʈ
	private ArrayList<Line> lineList; //line ����Ʈ
	

	public paintHandler(Socket socket, ArrayList<Room> roomList) {
		try {
			this.socket=socket;
			this.roomList=roomList;
			this.writer=new ObjectOutputStream(socket.getOutputStream());
			this.reader=new ObjectInputStream(socket.getInputStream());
			this.lineList=new ArrayList<Line>();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("initialMenu()?");
	}

	public void run() {
		try {
			while(true) {
				
				//Ŭ���̾�Ʈ���� ���� dto�� ���� �ȿ� �ִ� Handler���� ����
				paintDTO dto = (paintDTO)reader.readObject();
				boolean cflag = false;
				if(dto.getCommand()==Info.ROOMLIST) {
					sendRoomList();
				}
				
				else if(dto.getCommand()==Info.CREATE) {;
					String roomID=dto.getRoomID();
					String roomPW=dto.getRoomPW();
					String nickname=dto.getNickname();
					
					// �ߺ��Ǵ� roomID Ȯ�� 
					for(Room r:roomList) {
						// 1. �ߺ��� => �޽��� ����
						// (������ �ߺ� ������ �ȵȴٰ� ����)
						if(r.getRoomID().equals(roomID)) {
							System.out.println("�ߺ��ȴ�~!");
							
						}
						else
							cflag = true;
					}
				
					// 2. �ߺ� �� �� => �� ���� 
//					if(cflag) {
						handlerList=new ArrayList<paintHandler>();
						room=new Room(roomID,roomPW,handlerList);
						roomList.add(room);
						System.out.println("room "+roomID+" is created");
						
						room.enter(this);
						System.out.println(nickname+" is entered "+roomID);
//					}
				}
				else if(dto.getCommand()==Info.ENTER) {
					String roomID=dto.getRoomID();
					String roomPW=dto.getRoomPW();
					
					String nickname=dto.getNickname();
					int flag=0;
					
					// �ش� ���� �ִ��� Ȯ��
					for(Room r:roomList) {
						// 1. �� ã��
						if(r.getRoomID().equals(roomID)) {
								System.out.println("room "+roomID+" is found");
								r.enter(this);
								handlerList=r.getHandlerList();
								System.out.println(nickname+" is entered "+roomID);
								flag=1;
								
						}
						
					}

					// 2. �� ����
					// (������ �ߺ� ������ �� �ִٰ� ����)
					if(flag==0) {
						System.out.println("�� ����~!");
					}
				}
				//Ŭ���̾�Ʈ ���� �޼����� ��� dto�� Handler ����
				else if(dto.getCommand()==Info.EXIT) {
					
					handlerList.remove(this);
					System.out.println("exit client");
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(dto.getNickname()+"�� �����ϼ̽��ϴ�");
					broadcast(sendDTO);
					reader.close();
					writer.close();
					socket.close();
					break;
				}
				else if(dto.getCommand()==Info.JOIN) {
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage("["+dto.getNickname()+"] is entered");
					broadcast(sendDTO);
				}
				else if(dto.getCommand()==Info.SEND) {
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage("["+dto.getNickname()+"]: "+dto.getMessage());
					broadcast(sendDTO);
				}
				else if(dto.getCommand()==Info.DRAW) {
					
					//Ŭ���̾�Ʈ���� ���� dto�� ���Ͽ� ����� ��� Ŭ���̾�Ʈ�鿡�� ����
					broadcast(dto);
					
					line.add(dto.getB()); // line�� brush �߰�
				}
				else if(dto.getCommand()==Info.LINE_START) {
					line=new Line(); // line �Է� ����
					broadcast(dto);
					line.add(dto.getB());
				}
				else if(dto.getCommand()==Info.LINE_FINISH) {
					lineList.add(line); // line �Է� ���� -> lineList�� �߰�
					System.out.println("new line is added\n");
				}
				// FETCH�� ��û������ �ٸ� �ڵ鷯�� ������ �ִ� lineList�� �����ؼ�
				// ��û�� Ŭ���̾�Ʈ�� ����
				else if(dto.getCommand()==Info.FETCH) {
					for(paintHandler cho:handlerList) {
						if(cho==this) continue;
						for(Line line:cho.lineList) {
							for(Brush b:line) {
								paintDTO sendDTO=new paintDTO();
								sendDTO.setB(b);
								try {
									writer.writeObject(sendDTO);
									sendDTO.getB().print();
									writer.flush();
									writer.reset();
								}catch(IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					
				}
				else if(dto.getCommand() == Info.LAYER) {
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
	private void sendRoomList() {
		String roomlist="r.o.o.m.l.i.s.t/";
		  for(Room room : roomList){
		   String roomID=room.getRoomID();
		   roomlist+=roomID+"/";
		  }
		  roomlist+="\n";
		 paintDTO dto=new paintDTO();
		 dto.setCommand(Info.ROOMLIST);
		 dto.setRoomList(roomlist);
		 System.out.println("we will send roomlist: "+roomlist);
		 try {
				this.writer.writeObject(dto);
				this.writer.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}
	}
	private void broadcast(paintDTO dto) {
		for(paintHandler cho:handlerList) {
			try {
				if(cho != this) {
					cho.writer.writeObject(dto);
					cho.writer.flush();
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
