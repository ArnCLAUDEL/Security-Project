package certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BaseCertificationRequest {	
	private final static Random RAND = new Random();
	
	private final String subject;
	private final RSAPublicKeySpec pubKeySpec;
	
	public BaseCertificationRequest(String subject, RSAPublicKeySpec pubKeySpec) {
		this.subject = subject;
		this.pubKeySpec = pubKeySpec;
	}
	
	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		RSAPublicKeySpec spec = new RSAPublicKeySpec(getBig(), getBig());
		String subject = "Server of Services";
		BaseCertificationRequest bcr = new BaseCertificationRequest(subject, spec);
		bcr.writeTo(new FileOutputStream(new File("request")));
	}
	
	private static BigInteger getBig() {
		return BigInteger.valueOf(RAND.nextLong()).pow(1 << 4);
	}
	
	public String getSubject() {
		return subject;
	}

	public RSAPublicKeySpec getPubKeySpec() {
		return pubKeySpec;
	}
	
	public void writeTo(String filename) throws IOException {
		File file = new File(filename);
		FileOutputStream out = new FileOutputStream(file);
		writeTo(out);
	}

	public void writeTo(OutputStream dst) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(dst);	
		
		out.writeObject(subject);
		
		byte[] array;
		
		array = pubKeySpec.getModulus().toByteArray();
		out.writeInt(array.length);
		out.write(array);
		
		array = pubKeySpec.getPublicExponent().toByteArray();
		out.writeInt(array.length);
		out.write(array);
		
		out.close();
	}
	
	public static BaseCertificationRequest readFrom(String filename) throws IOException, ClassNotFoundException {
		File file = new File(filename);
		FileInputStream in = new FileInputStream(file);
		return readFrom(in);
	}

	public static BaseCertificationRequest readFrom(InputStream from) throws IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(from);
		
		String subject = (String) in.readObject();
		
		int length;
		byte[] array;
		
		length = in.readInt();
		array = new byte[length];
		in.read(array);
		BigInteger modulus = new BigInteger(array);
		
		length = in.readInt();
		array = new byte[length];
		in.read(array);
		BigInteger exponent = new BigInteger(array);
		
		in.close();
		
		return new BaseCertificationRequest(subject, new RSAPublicKeySpec(modulus, exponent));
	}
	
}
