package protocol.message.certification;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import protocol.Flag;
import protocol.message.EncryptedMessage;
import util.Creator;
import util.SerializerBuffer;

public class AuthRequest extends AbstractCertificationMessage implements EncryptedMessage {
	public final static Creator<AuthRequest> CREATOR = AuthRequest::new;
	
	private String filename;
	private String alias;
	
	private AuthRequest() {
		super(Flag.AUTH_REQUEST);
	}
	
	public AuthRequest(long id, String filename, String alias) {
		super(Flag.AUTH_REQUEST, id);
		this.filename = filename;
		this.alias = alias;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getAlias() {
		return alias;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		ms.putString(filename);
		ms.putString(alias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.id = ms.getLong();
		this.filename = ms.getString();
		this.alias = ms.getString();
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SerializerBuffer serializer = new SerializerBuffer();
		serializer.putLong(id);
		serializer.putString(filename);
		serializer.putString(alias);
		serializer.flip();
		cipher.doFinal(serializer.getBuffer(), ms.getBuffer());
	}

	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		SerializerBuffer serializer = new SerializerBuffer();
		cipher.doFinal(ms.getBuffer(), serializer.getBuffer());
		serializer.flip();
		id = serializer.getLong();
		filename = serializer.getString();
		alias = serializer.getString();
	}

}
