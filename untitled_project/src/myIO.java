import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class myIO {
	private serialTransform st;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public myIO(ObjectOutputStream w, ObjectInputStream r) {
		this.writer = w;
		this.reader = r;
		
		this.dos = new DataOutputStream(this.writer);
		this.dis = new DataInputStream(this.reader);
		
		st = new serialTransform();
	}
	
	public DTO myRead() throws IOException, ClassNotFoundException {
		DTO temp = null;
		byte[] buffer = null;
		
		int len = reader.readInt();

		System.out.println("myRead len : " + len);
		//System.out.println("myRead base64Member : " + base64Member);
		
		//String base64Member = (String) reader.readObject();
		//temp = (DTO) st.decrypt(base64Member);
		
		return temp;
	}
	
	public void myWrite(DTO dto) throws IOException {
		
		//String base64Member = st.encrypt(dto);
		String base64Member = st.encrypt(dto);
		int len = base64Member.length();
		
		//String base64Member = Base64.getEncoder().encodeToString(buffer);
		
		System.out.println("myWrite len : " + len);
		//System.out.println("myWrite buffer : " + buffer);
		System.out.println("myWrite base64Member : " + base64Member);
		
		writer.writeInt(len);
		writer.writeObject(base64Member);
		//writer.writeObject(base64Member);
	}

}
