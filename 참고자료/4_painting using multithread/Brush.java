import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;

public class Brush  extends JLabel{
	private int xx,yy;
	private Color col;
	private int dia;
	
	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public Brush(Color col, int dia) {
		this.col = col;
		this.dia=dia;
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
		g.fillOval(xx-dia/2, yy-dia/2, dia, dia);
	}
}
