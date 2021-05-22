import java.util.ArrayList;



public class Room {
	private String roomID;
	private String PW;
	private String nickName;
	private int userCnt;
	private int maxUser;
	private ArrayList<paintHandler> handlerList;
	
	public ArrayList<paintHandler> getHandlerList() {
		return handlerList;
	}

	public void setHandlerList(ArrayList<paintHandler> handlerList) {
		this.handlerList = handlerList;
	}

	private paintHandler admin;
	
	public Room(String roomName, String PW,ArrayList<paintHandler> handlerList) {
		this.roomID=roomName;
		this.PW=PW;
		this.handlerList=handlerList;
		this.userCnt=0;
	}
	
	public void enter(paintHandler handler) {
		setUserCnt(getUserCnt()+1);
		handlerList.add(handler);
	}
	
	public void setnickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setroomName(String rN) {
		this.roomID = rN;
	}
	public void setPW(String pw) {
		this.PW = pw;
	}
	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}
	
	public String getRoomID() {
		return this.roomID;
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

	public void join(String nickname2) {
		// TODO Auto-generated method stub
		
	}

	public int getUserCnt() {
		return userCnt;
	}

	public void setUserCnt(int userCnt) {
		this.userCnt = userCnt;
	}

	
}
