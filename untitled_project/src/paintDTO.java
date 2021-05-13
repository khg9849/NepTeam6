
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

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
	
	ROOMLIST, ROOMSETTINGALLCLEAR
}

public class paintDTO implements Serializable {
	private static final long serialVersionUID=-1702115601182003730L;
	
	private Info command; // DTO type
	
	private Brush b;
	private BrushMode brushMode;
	private boolean isAddLayer;
	private int selectedLayerIndex;
	
	private String message;
	private String nickname;
	
	private String roomID;
	private String roomPW;
	
	private String roomList;
	
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
	

	public Brush getB() {
		return b;
	}

	public void setB(Brush b) {
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

	
}
