import java.util.ArrayList;



public class roomStat {
	String roomName;
	String PW;
	String nickName;
	int maxUser;
	ArrayList<paintHandler> handle = new ArrayList();
	
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
	
	
}
