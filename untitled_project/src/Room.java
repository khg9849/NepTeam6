import java.util.ArrayList;



public class Room {
	private String roomName;
	private String PW;
	private String nickName;
	private int maxUser;
	private ArrayList<paintHandler> handlerList;
	private paintHandler admin;
	
	public Room(String roomName, String PW, paintHandler admin ) {
		this.roomName=roomName;
		this.PW=PW;
		this.setAdmin(admin);
		handlerList=new ArrayList<paintHandler>();
		handlerList.add(admin);
	}
	
	public void setnickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setroomName(String rN) {
		this.roomName = rN;
	}
	public void setPW(String pw) {
		this.PW = pw;
	}
	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}
	
	public String getroomName() {
		return this.roomName;
	}
	public String getnickName() {
		return this.nickName;
	}
	public String getPW() {
		return this.PW;
	}
	public int getmaxUser() {
		return this.maxUser;
	}

	public paintHandler getAdmin() {
		return admin;
	}

	public void setAdmin(paintHandler admin) {
		this.admin = admin;
	}
	
	
}
