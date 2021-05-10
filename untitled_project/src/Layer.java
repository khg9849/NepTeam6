import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Layer extends BufferedImage{
	private String name;
	private int priority;
	
	public Layer(String n) {
		super(400, 400, BufferedImage.TYPE_INT_ARGB );
		name = n;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPriority(int p) {
		priority = p;
	}
	
	public int getPriority() {
		return priority;
	}
}
