import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

public class Brush  extends JLabel{
	private int xx,yy;
	private Color col;
	
	public Brush(int r, int g, int b) {
		this.col = new Color(r,g,b);
	}
	
	public void print() {
		System.out.println("xx: "+xx+" yy: "+yy);
	}
	public int getXx() {
		return xx;
	}

	public void setXx(int xx) {
		this.xx = xx;
	}

	public int getYy() {
		return yy;
	}

	public void setYy(int yy) {
		this.yy = yy;
	}

	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}
	
	public void setCol(int r, int g, int b) {
		this.col = new Color(r, g, b);
	}
	
	public void paint(Graphics g) {
		g.setColor(col);
		g.fillOval(xx-10, yy-10, 20, 20);
	}
}
