import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class serialTransform {
	
	//���� ��ü Class�� String���� return��Ų��.
	public String encrypt(Object o) {
		
		String base64Member = null;
		byte[] serializedMember = null;
		
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			try(ObjectOutputStream oos = new ObjectOutputStream(baos)){
				oos.writeObject(o);
				serializedMember = baos.toByteArray();
			}catch(IOException e1) {}
		}catch(IOException e1) {}
		
		base64Member = 	Base64.getEncoder().encodeToString(serializedMember);
		
		return base64Member;
	}
	
	//���� String�� ��ü Class�� return��Ų��.
	public Object decrypt(String s) {
		Object o = null;
		String base64Member = s;
		
		byte[] serializedMember = Base64.getDecoder().decode(base64Member);
		try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedMember)){
			try(ObjectInputStream ois = new ObjectInputStream(bais)){
				Object objectMember = ois.readObject();
				o = objectMember;
			}catch(IOException e1) {
			}catch(ClassNotFoundException e2) {}
		}catch(IOException e1) {}
		
		return o;
	}

}
