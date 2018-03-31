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
import util.Creator;
import util.SerializerBuffer;

public class SessionInit extends AbstractSessionMessage {
	public final static Creator<SessionInit> CREATOR = SessionInit::new;
	
	private SecretKey secretKey;
	private String senderAlias;
	
	private SessionInit() {
		super(Flag.SESSION_INIT);
	}
	
	public SessionInit(long id, SecretKey secretKey, String senderAlias) {
		super(Flag.SESSION_INIT, id);
		this.secretKey = secretKey;
		this.senderAlias = senderAlias;
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
	}

	public String getSenderAlias() {
		return senderAlias;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		clearBuffer.putLong(id);
		clearBuffer.putString(senderAlias);
		clearBuffer.putByteArray(secretKey.getEncoded());
		clearBuffer.flip();
		cipher.doFinal(clearBuffer.getBuffer(), ms.getBuffer());
	}

	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		cipher.doFinal(ms.getBuffer(), clearBuffer.getBuffer());
		clearBuffer.flip();
		id = clearBuffer.getLong();
		senderAlias = clearBuffer.getString();
		byte[] encodedKey = clearBuffer.getByteArray();
		secretKey = new SecretKeySpec(encodedKey, "AES");
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		ms.putString(senderAlias);
		ms.putByteArray(secretKey.getEncoded());
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		senderAlias = ms.getString();
		byte[] encodedKey = ms.getByteArray();
		secretKey = new SecretKeySpec(encodedKey, "AES");
	}
	
}
