package protocol.message.session;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import protocol.Flag;
import protocol.Nonce;
import util.Creator;
import util.SerializerBuffer;

public class SessionReply extends AbstractSessionMessage {
	public final static Creator<SessionReply> CREATOR = SessionReply::new;
	
	private Nonce senderNonce;
	private String destinationAlias;
	private SecretKey secretKey;
	private byte[] encodedMessage;
	
	private SessionReply() {
		super(Flag.SESSION_REPLY);
	}
	
	public SessionReply(long id, Nonce senderNonce, String destinationAlias, byte[] encodedMessage, SecretKey secretKey) {
		this();
		this.id = id;
		this.senderNonce = senderNonce;
		this.destinationAlias = destinationAlias;
		this.secretKey = secretKey;
		this.encodedMessage = encodedMessage;
	}
	
	public Nonce getSenderNonce() {
		return senderNonce;
	}

	public String getDestinationAlias() {
		return destinationAlias;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public byte[] getEncodedMessage() {
		return encodedMessage;
	}
	
	public void writeToBuff(SerializerBuffer ms, Cipher rsaCipher) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {				
		SerializerBuffer buffer = new SerializerBuffer();
				
		byte[] encryptedSKey = rsaCipher.doFinal(secretKey.getEncoded());	
		ms.putByteArray(encryptedSKey);
		
		System.err.println(id);
		buffer.putLong(id);
		senderNonce.writeToBuff(buffer);
		buffer.putString(destinationAlias);
		buffer.putByteArray(encodedMessage);
		buffer.flip();
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		aesCipher.doFinal(buffer.getBuffer(), ms.getBuffer());
	}
	
	public void readFromBuff(SerializerBuffer ms, Cipher rsaCipher) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {				
		SerializerBuffer serializer = new SerializerBuffer();
		byte[] encryptedKey, decryptedKey;
		encryptedKey = ms.getByteArray();
		decryptedKey = rsaCipher.doFinal(encryptedKey);
		
		secretKey = new SecretKeySpec(decryptedKey, "AES");
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
		
		aesCipher.doFinal(ms.getBuffer(), serializer.getBuffer());
		serializer.flip();
		
		System.err.println(id);
		id = serializer.getLong();
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(serializer);
		System.err.println(serializer);
		destinationAlias = serializer.getString();
		System.err.println(serializer);
		encodedMessage = serializer.getByteArray();
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {		
		ms.putLong(id);
		senderNonce.writeToBuff(ms);
		ms.putString(destinationAlias);
		ms.putByteArray(encodedMessage);
		ms.putByteArray(secretKey.getEncoded());
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(ms);
		destinationAlias = ms.getString();
		encodedMessage = ms.getByteArray();
		byte[] encodedKey = ms.getByteArray();
		secretKey = new SecretKeySpec(encodedKey, "AES");
	}

}
