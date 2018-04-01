package certification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;

/**
 * {@code BaseCertificationRequest} represents a lightweight certification request.
 * It only contains two informations : the subject's name and the subject's {@link RSAPublicKeySpec}.<br />
 * This object can easily be read/written from/to a file.
 */
public class BaseCertificationRequest {	
	
	/**
	 * The subject's name
	 */
	private final String subject;
	
	/**
	 * The subject's public key
	 */
	private final RSAPublicKeySpec pubKeySpec;
	
	/**
	 * Creates a new instance with the given subject's name and {@link RSAPublicKeySpec}.
	 * @param subject The subject's name
	 * @param pubKeySpec The subject's public key
	 */
	public BaseCertificationRequest(String subject, RSAPublicKeySpec pubKeySpec) {
		this.subject = subject;
		this.pubKeySpec = pubKeySpec;
	}
	
	/**
	 * Returns the subject's name.
	 * @return The subject's name
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Returns the suject's public key.
	 * @return The subject's public key
	 */
	public RSAPublicKeySpec getPubKeySpec() {
		return pubKeySpec;
	}
	
	/**
	 * Writes this request to the given file.<br />
	 * The subject's name is first written, then it's followed by
	 * the modulus and public exponent of the subject's public key.
	 * @param filename The file to write to
	 * @throws IOException
	 */
	public void writeTo(String filename) throws IOException {
		File file = new File(filename);
		FileOutputStream out = new FileOutputStream(file);
		writeTo(out);
	}

	/**
	 * Writes this request to the given {@link OutputStream}.<br />
	 * The subject's name is first written, then it's followed by
	 * the modulus and public exponent of the subject's public key.
	 * @param dst The outputStream to write to
	 * @throws IOException
	 */
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
	
	/**
	 * Reads the request from the given file.<br />
	 * The subject's name is first read, then it's followed by
	 * the modulus and public exponent of the subject's public key.
	 * @param filename The file to read from
	 * @return The request with the informations from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static BaseCertificationRequest readFrom(String filename) throws IOException, ClassNotFoundException {
		File file = new File(filename);
		FileInputStream in = new FileInputStream(file);
		return readFrom(in);
	}

	/**
	 * Reads the request from the input source.<br />
	 * The subject's name is first read, then it's followed by
	 * the modulus and public exponent of the subject's public key.
	 * @param filename The file to read from
	 * @return The request with the informations from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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
