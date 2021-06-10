import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class myIO {
	private serialTransform st;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	
	private DataOutputStream dos;
	private DataInputStream dis;
	private ExecutorService executorService;
	
	public myIO(ObjectOutputStream w, ObjectInputStream r) {
		this.writer = w;
		this.reader = r;
		
		this.dos = new DataOutputStream(this.writer);
		this.dis = new DataInputStream(this.reader);
		
		st = new serialTransform();
		executorService = Executors.newFixedThreadPool(10);
	}
	
	public DTO myRead() throws IOException, ClassNotFoundException {

		
		DTO temp = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		
		int len = dis.readInt();
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

			int r;
			String base64Member = null;
			int returnLength = len;
			byte[] reciveData = new byte[len];
			try {
				while((r = dis.read(reciveData, 0, reciveData.length))!= -1) {
					buffer.write(reciveData, 0 , r);
				    returnLength = returnLength-r;
				    if (returnLength<=0){
				        break;
				    }
				}
				buffer.flush();
				base64Member = buffer.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return base64Member;
			
		}, executorService);
		
		
		
		try {
			temp = (DTO) st.decrypt(future.get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temp;
	}
	
	synchronized public void myWrite(DTO dto) throws IOException  {
		
		//String base64Member = st.encrypt(dto);
		String base64Member = st.encrypt(dto);
		int len = base64Member.length();
		
		
		byte[] buffer = base64Member.getBytes();
		
		dos.writeInt(len);
		dos.write(buffer);
		dos.flush();
	}

}
