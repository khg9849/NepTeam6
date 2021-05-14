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
	
	private String nickname;
	
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
						// (������ �ߺ� ������ �ȵȴٰ� ����)
						if(r.getRoomID().equals(roomID)) {
							System.out.println("�ߺ��ȴ�~!");
							cflag = false;
						}
						
					}
				
					// 2. �ߺ� �� �� => �� ���� 
					if(cflag) {

						handlerList=new ArrayList<paintHandler>();
						room=new Room(roomID,roomPW,handlerList);
						roomList.add(room);
						System.out.println("room "+roomID+" is created");
						
						room.enter(this);
						System.out.println(nickname+" is entered "+roomID);
						
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
				//EXIT(2): Ŭ���̾�Ʈ���Լ� EXIT1�� ������ �ٽ� EXIT2 ����
				else if(dto.getCommand()==Info.EXIT1) {
					System.out.println("receive EXIT1 from "+dto.getNickname());
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.EXIT2);
					try {
						writer.writeObject(sendDTO);
						writer.flush();
						writer.reset();
					}catch(IOException e) {
						e.printStackTrace();
					}
					System.out.println("send EXIT2 to "+dto.getNickname());
				}
				//EXIT(4): Ŭ���̾�Ʈ���Լ� EXIT3�� ������ ���� ����, handlerList���� ����
				// 			�ش� Ŭ���̾�Ʈ�� ���� �����ٴ� ���� EXIT4�� ��� broadcast
				else if(dto.getCommand()==Info.EXIT3) {
					System.out.println("receive EXIT3 from "+dto.getNickname());
					reader.close();
					writer.close();
					socket.close();
					handlerList.remove(this);
					
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.EXIT4);
					sendDTO.setNickname(nickname);
					broadcast(sendDTO);
					System.out.println("broadcast EXIT4");
					break;
				}
				else if(dto.getCommand()==Info.EXIT) {
					
					handlerList.remove(this);
					System.out.println("exit client");
					
					reader.close();
					writer.close();
					socket.close();
					
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(dto.getNickname()+"�� �����ϼ̽��ϴ�");
					broadcast(sendDTO);
					
					break;
				}
				else if(dto.getCommand()==Info.JOIN) {
					paintDTO sendDTO=new paintDTO();
					nickname=dto.getNickname();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage("["+nickname+"] is entered");
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
		String roompwlist = "p.w.l.i.s.t/";
		for(Room room : roomList){
			String roomID=room.getRoomID();
			String roomPW=room.getPW();
			roomlist+=roomID+"/";
			roompwlist+=roomPW+"/";
		}
		roomlist+="\n";
		roompwlist+="\n";
		paintDTO dto=new paintDTO();
		dto.setCommand(Info.ROOMLIST);
		dto.setRoomList(roomlist);
		dto.setRoomPwList(roompwlist);
		System.out.println("we will send roomlist: "+roomlist);
		System.out.println("we will send roompwlist: "+roompwlist);
		try {
			this.writer.writeObject(dto);
			this.writer.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void broadcast(paintDTO dto) {
		if(handlerList.isEmpty()) return;
		System.out.println("handlerList is not empty. so server can broadcast");
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
