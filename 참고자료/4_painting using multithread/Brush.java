import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

public class Brush  extends JLabel{
	private int xx,yy;
	
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

	private Color col=new Color(255,0,0);
	
	public void paint(Graphics g) {
		g.setColor(col);
		g.fillOval(xx-10, yy-10, 20, 20);
	}
}
