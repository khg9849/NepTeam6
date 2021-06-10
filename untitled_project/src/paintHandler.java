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
	
	private Line line; //핸들러가 받은 brush 리스트
	private ArrayList<Line> lineList; //line 리스트
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
				
				//클라이언트에서 보낸 dto는 서버 안에 있는 Handler들이 받음
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
					// 중복되는 roomID 확인 
					for(Room r:roomList) {
						System.out.println("for\n");
						// 1. 중복됨 => 메시지 띄우기
						if(r.getRoomID().equals(roomID)) {
							System.out.println("방 이름이 중복된다.");
							cflag = false;
						}
						
					}
				
					// 2. 중복 안 됨 => 방 생성 
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
					
					// 해당 방이 있는지 확인
					for(Room r:roomList) {
						// 1. 방 찾음
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
				//EXIT(1): 클라이언트에게서 EXIT1을 받으면 다시 EXIT1 전송
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
				//EXIT(2): 클라이언트에게서 EXIT2을 받으면 소켓 종료, handlerList에서 삭제
				// 			해당 클라이언트가 방을 나갔다는 정보 EXIT3에 담아 broadcast
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
					//클라이언트에서 받은 dto를 소켓에 연결된 모든 클라이언트들에게 보냄
					broadcast(dto);
				}
				else if(dto.getCommand()==Info.LINE_START) {
					broadcast(dto);
				}
				else if(dto.getCommand()==Info.LINE_FINISH) {
					lineList.add(line); // line 입력 종료 -> lineList에 추가
					System.out.println("new line is added\n");
				}
				// FETCH를 요청받으면 다른 핸들러가 가지고 있는 lineList를 참조해서
				// 요청한 클라이언트에 전송
				else if(dto.getCommand()==Info.FETCH) {
					paintHandler temp = handlerList.get(0);
					if(temp != this) {
						//room에 있는 paintHandler의 배열에서 index==0에게 FETCH발송
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
					//index 0의 핸들러는 해당 클라가 가지고 있는 레이어 정보를 통째로 받음
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
