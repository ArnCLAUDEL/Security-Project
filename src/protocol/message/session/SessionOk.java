package protocol.message.session;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import protocol.Flag;
import protocol.Nonce;
import util.Creator;
import util.SerializerBuffer;

public class SessionOk extends AbstractSessionMessage {
	public final static Creator<SessionOk> CREATOR = SessionOk::new;
	
	private Nonce senderNonce;
	
	private SessionOk() {
		super(Flag.SESSION_OK);
	}
	
	public SessionOk(long id, Nonce senderNonce) {
		super(Flag.SESSION_OK, id);
		this.senderNonce = senderNonce;
	}
	
	public Nonce getSenderNonce() {
		return senderNonce;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
			SerializerBuffer clearBuffer = new SerializerBuffer();
			clearBuffer.putLong(id);
			senderNonce.writeToBuff(clearBuffer);
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
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(clearBuffer);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		senderNonce.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(ms);
	}
	
}
