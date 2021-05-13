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
	
	private Line line; //핸들러가 받은 brush 리스트
	private ArrayList<Line> lineList; //line 리스트
	

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
				
				//클라이언트에서 보낸 dto는 서버 안에 있는 Handler들이 받음
				paintDTO dto = (paintDTO)reader.readObject();
				boolean cflag = false;
				if(dto.getCommand()==Info.ROOMLIST) {
					sendRoomList();
				}
				
				else if(dto.getCommand()==Info.CREATE) {;
					String roomID=dto.getRoomID();
					String roomPW=dto.getRoomPW();
					String nickname=dto.getNickname();
					
					// 중복되는 roomID 확인 
					for(Room r:roomList) {
						// 1. 중복됨 => 메시지 띄우기
						// (지금은 중복 무조건 안된다고 가정)
						if(r.getRoomID().equals(roomID)) {
							System.out.println("중복된다~!");
							
						}
						else
							cflag = true;
					}
				
					// 2. 중복 안 됨 => 방 생성 
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
					
					// 해당 방이 있는지 확인
					for(Room r:roomList) {
						// 1. 방 찾음
						if(r.getRoomID().equals(roomID)) {
								System.out.println("room "+roomID+" is found");
								r.enter(this);
								handlerList=r.getHandlerList();
								System.out.println(nickname+" is entered "+roomID);
								flag=1;
								
						}
						
					}

					// 2. 방 없음
					// (지금은 중복 무조건 방 있다고 가정)
					if(flag==0) {
						System.out.println("방 없다~!");
					}
				}
				//클라이언트 종료 메세지가 담긴 dto면 Handler 종료
				else if(dto.getCommand()==Info.EXIT) {
					
					handlerList.remove(this);
					System.out.println("exit client");
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(dto.getNickname()+"님 퇴장하셨습니다");
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
					
					//클라이언트에서 받은 dto를 소켓에 연결된 모든 클라이언트들에게 보냄
					broadcast(dto);
					
					line.add(dto.getB()); // line에 brush 추가
				}
				else if(dto.getCommand()==Info.LINE_START) {
					line=new Line(); // line 입력 시작
					broadcast(dto);
					line.add(dto.getB());
				}
				else if(dto.getCommand()==Info.LINE_FINISH) {
					lineList.add(line); // line 입력 종료 -> lineList에 추가
					System.out.println("new line is added\n");
				}
				// FETCH를 요청받으면 다른 핸들러가 가지고 있는 lineList를 참조해서
				// 요청한 클라이언트에 전송
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
