
import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import javax.swing.JLabel;

enum Info{
	//그림
	DRAW,LINE_START,LINE_FINISH,
	LAYER,
	
	//채팅
	JOIN,SEND,
	
	//참여 및 종료
	EXIT, FETCH,
	
	//ROOM
	CREATE,ENTER,
	
	ROOMLIST,
	
	//종료 //EXIT은 이제 필요없다 나중에 지우자
	EXIT1, EXIT2, EXIT3, EXIT4
}

public class paintDTO implements Serializable {
	private static final long serialVersionUID=21L;
	
	private Info command; // DTO type
	
	private String b;
	private BrushMode brushMode;
	private boolean isAddLayer;
	private int selectedLayerIndex;
	
	private String message;
	private String nickname;
	
	private String roomID;
	private String roomPW;
	private int userCnt;
	
	private String roomList;
	private String roompwList;
	
	//Brush 클래스를 직렬화 및 역직렬화
	
	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public String getRoomPW() {
		return roomPW;
	}

	public void setRoomPW(String roomPW) {
		this.roomPW = roomPW;
	}
	
	paintDTO(){
		command=Info.DRAW;
	}
	
	public String getB(){
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}
	
	public BrushMode getBrushMode() {
		return brushMode;
	}
	
	public void setBrushMode(BrushMode brushMode2) {
		brushMode = brushMode2;
	}
	
	public boolean getLayerBoolean() {
		return isAddLayer;
	}
	
	public void setLayerBoolean(boolean bn) {
		isAddLayer = bn;
	}
	
	public int getL() {
		return selectedLayerIndex;
	}
	
	public void setL(int index) {
		selectedLayerIndex = index;
	}

	public Info getCommand() {
		return command;
	}

	public void setCommand(Info command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRoomList() {
		return roomList;
	}

	public void setRoomList(String roomList) {
		this.roomList = roomList;
	}
	
	public String getRoomPwList() {
		return roompwList;
	}

	public void setRoomPwList(String roompwList) {
		this.roompwList = roompwList;
	}

	public int getUserCnt() {
		return userCnt;
	}

	public void setUserCnt(int userCnt) {
		this.userCnt = userCnt;
	}
	
}
