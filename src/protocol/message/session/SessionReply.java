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
	
	public SessionReply(Nonce senderNonce, String destinationAlias, byte[] encodedMessage, SecretKey secretKey) {
		this();
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
		SerializerBuffer buffer = new SerializerBuffer(2048);
				
		buffer.putByteArray(secretKey.getEncoded());
		buffer.flip();
		rsaCipher.doFinal(buffer.getBuffer(), ms.getBuffer());	
		buffer.clear();
		
		senderNonce.writeToBuff(buffer);
		buffer.putString(destinationAlias);
		buffer.putByteArray(encodedMessage);
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		aesCipher.doFinal(buffer.getBuffer(), ms.getBuffer());
	}
	
	public void readFromBuff(SerializerBuffer ms, Cipher rsaCipher) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {				
		SerializerBuffer buffer = new SerializerBuffer(2048);
		
		byte[] encryptedKey, decryptedKey;
		encryptedKey = ms.getByteArray();
		decryptedKey = rsaCipher.doFinal(encryptedKey);
		
		System.err.println(decryptedKey);
		
		secretKey = new SecretKeySpec(decryptedKey, "AES");
		
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
		
		aesCipher.doFinal(ms.getBuffer(), buffer.getBuffer());
		buffer.flip();
		
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(buffer);
		destinationAlias = buffer.getString();
		encodedMessage = buffer.getByteArray();
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
