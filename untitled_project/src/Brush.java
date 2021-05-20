import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.JLabel;

public class Brush  extends JLabel implements Serializable{
	private static final long serialVersionUID=22L;
	private int xx,yy;	//현재 위치
	private int x, y;	//바로 이전의 위치
	private Color col;
	private int dia;
	private boolean isMouseDrag;
	
	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}
	
	public boolean get_isMouseDrag() {
		return isMouseDrag;
	}
	
	public void set_isMouseDrag(boolean t) {
		this.isMouseDrag = t; 
	}

	public Brush(Color col, int dia) {
		this.col = col;
		this.dia=dia;
		this.isMouseDrag = false;
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
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
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
		
		//MousePressed면 점으로 찍기
		if( this.isMouseDrag == false)
			g.fillOval(xx-dia/2, yy-dia/2, dia, dia);
		
		//MouseDragged면 선으로 잇기
		if (this.isMouseDrag == true){
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(dia,BasicStroke.CAP_ROUND,0));
			g.drawLine(x, y, xx, yy);
		}
			
	}
}
