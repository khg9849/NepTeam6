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
	
	private Line line; //핸들러가 받은 brush 리스트
	private ArrayList<Line> lineList; //line 리스트
	
	public paintHandler(Socket socket, ArrayList<paintHandler> list) {
		try {
			this.socket=socket;
			this.list=list;
			this.writer=new ObjectOutputStream(socket.getOutputStream());
			this.reader=new ObjectInputStream(socket.getInputStream());
			this.lineList=new ArrayList<Line>();
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
					
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(dto.getNickname()+"님 퇴장하셨습니다");
					
					broadcast(sendDTO);
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
					for(paintHandler cho:list) {
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

	private void broadcast(paintDTO dto) {
		for(paintHandler cho:list) {
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
