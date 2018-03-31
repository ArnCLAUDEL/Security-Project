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

public class SessionRequest extends AbstractSessionMessage {
	public final static Creator<SessionRequest> CREATOR = SessionRequest::new;
	
	private Nonce senderNonce;
	private String senderAlias;
	private String destinationAlias;
	
	private SessionRequest() {
		super(Flag.SESSION_REQUEST);
	}
	
	public SessionRequest(long id, Nonce senderNonce, String senderAlias, String destinationAlias) {
		super(Flag.SESSION_REQUEST, id);
		this.senderNonce = senderNonce;
		this.senderAlias = senderAlias;
		this.destinationAlias = destinationAlias;
	}
	
	public Nonce getSenderNonce() {
		return senderNonce;
	}
	
	public String getSenderAlias() {
		return senderAlias;
	}
	
	public String getDestinationAlias() {
		return destinationAlias;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		System.err.println(id);
		ms.putLong(id);
		senderNonce.writeToBuff(ms);
		ms.putString(senderAlias);
		ms.putString(destinationAlias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		System.err.println(id);
		senderNonce = Nonce.CREATOR.init();
		senderNonce.readFromBuff(ms);
		senderAlias = ms.getString();
		destinationAlias = ms.getString();
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		readFromBuff(ms);
	}

}
