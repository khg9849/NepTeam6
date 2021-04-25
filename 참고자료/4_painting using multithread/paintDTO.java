
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JLabel;

enum Info{
	DRAW,EXIT
}

public class paintDTO implements Serializable {
	private static final long serialVersionUID=-1702115601182003730L;
	private Brush b;
	private Info command;
	
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
	
}
