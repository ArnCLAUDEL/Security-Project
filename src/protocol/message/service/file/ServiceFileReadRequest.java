package protocol.message.service.file;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import protocol.Flag;
import session.client.SessionIdentifier;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileReadRequest extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileReadRequest> CREATOR = ServiceFileReadRequest::new;
	
	private ServiceFileReadRequest() {
		super(Flag.SERVICE_FILE_READ_REQUEST);
	}
	
	public ServiceFileReadRequest(long id, String filename, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_READ_REQUEST, id, filename, sessionIdentifier);
	}
	
	public ServiceFileReadRequest(long id, String filename, String errorMessage, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_READ_REQUEST, id, filename, errorMessage, sessionIdentifier);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		super.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		super.readFromBuff(ms);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		ms.putLong(id);
		ms.putLong(sessionIdentifier.getId());
		System.out.println(id + " | " + sessionIdentifier.getId());
		System.out.println(Long.toUnsignedString(sessionIdentifier.getId()));
		sessionIdentifier.writeToBuff(clearBuffer);
		clearBuffer.putString(filename);
		if(errorMessage.isPresent()) {
			clearBuffer.put(ERROR);
			clearBuffer.putString(errorMessage.get());
		} else {
			clearBuffer.put(OK);
		}
		clearBuffer.flip();
		cipher.doFinal(clearBuffer.getBuffer(), ms.getBuffer());
	}
	
	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		id = ms.getLong();
		long sessionId = ms.getLong();
		System.err.println(id + " | " + sessionId);
		cipher.doFinal(ms.getBuffer(), clearBuffer.getBuffer());
		clearBuffer.flip();
		sessionIdentifier = SessionIdentifier.CREATOR.init();
		sessionIdentifier.readFromBuff(clearBuffer);
		filename = clearBuffer.getString();
		byte flag = clearBuffer.get();
		if(flag == ERROR) {
			this.errorMessage = Optional.of(clearBuffer.getString());
		}
	}

}
