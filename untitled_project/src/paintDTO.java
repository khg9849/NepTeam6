
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JLabel;

enum Info{
	DRAW,LINE_START,LINE_FINISH,
	JOIN,SEND,
	EXIT, FETCH
}

public class paintDTO implements Serializable {
	private static final long serialVersionUID=-1702115601182003730L;
	
	private Info command; // DTO type
	
	private Brush b;
	
	private String message;
	private String nickname;
	
	paintDTO(){
		command=Info.DRAW;
	}

	public Brush getB() {
		return b;
	}

	public void setB(Brush b) {
		this.b = b;
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
	
}
