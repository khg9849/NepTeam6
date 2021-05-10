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
	
	private Line line; //�ڵ鷯�� ���� brush ����Ʈ
	private ArrayList<Line> lineList; //line ����Ʈ
	
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
				//Ŭ���̾�Ʈ���� ���� dto�� ���� �ȿ� �ִ� Handler���� ����
				paintDTO dto = (paintDTO)reader.readObject();
				
				//Ŭ���̾�Ʈ ���� �޼����� ��� dto�� Handler ����
				if(dto.getCommand()==Info.EXIT) {
					reader.close();
					writer.close();
					socket.close();
					list.remove(this);
					System.out.println("exit client");
					
					paintDTO sendDTO=new paintDTO();
					sendDTO.setCommand(Info.SEND);
					sendDTO.setMessage(dto.getNickname()+"�� �����ϼ̽��ϴ�");
					
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
