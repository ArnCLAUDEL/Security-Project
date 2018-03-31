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

public class SessionAck extends AbstractSessionMessage {
	public final static Creator<SessionAck> CREATOR = SessionAck::new;
	
	private Nonce destinationNonce;
	
	private SessionAck() {
		super(Flag.SESSION_ACK);
	}
	
	public SessionAck(long id, Nonce destinationNonce) {
		super(Flag.SESSION_ACK, id);
		this.destinationNonce = destinationNonce;
	}
	
	public Nonce getDestinationNonce() {
		return destinationNonce;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		
		ms.putLong(id);
		destinationNonce.writeToBuff(clearBuffer);
		clearBuffer.flip();
		cipher.doFinal(clearBuffer.getBuffer(), ms.getBuffer());
	}

	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		id = ms.getLong();
		cipher.doFinal(ms.getBuffer(), clearBuffer.getBuffer());
		clearBuffer.flip();
		destinationNonce = Nonce.CREATOR.init();
		destinationNonce.readFromBuff(clearBuffer);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		destinationNonce.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		destinationNonce = Nonce.CREATOR.init();
		destinationNonce.readFromBuff(ms);
	}
	
}
