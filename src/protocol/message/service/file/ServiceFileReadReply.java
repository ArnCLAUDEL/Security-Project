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

public class ServiceFileReadReply extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileReadReply> CREATOR = ServiceFileReadReply::new;
	
	private String content;
	
	private ServiceFileReadReply() {
		super(Flag.SERVICE_FILE_READ_REPLY);
	}
	
	public ServiceFileReadReply(String filename, String content, long id, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_READ_REPLY, id, filename, sessionIdentifier);
		this.content = content;
	}
	
	public ServiceFileReadReply(long id, String filename, String content, String errorMessage, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_READ_REPLY, id, filename, errorMessage, sessionIdentifier);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	@Override
	public void writeToBuff(util.SerializerBuffer ms, Cipher cipher)
			throws ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		ms.putLong(id);
		ms.putLong(sessionIdentifier.getId());
		sessionIdentifier.writeToBuff(clearBuffer);
		clearBuffer.putString(filename);
		if(errorMessage.isPresent()) {
			clearBuffer.put(ERROR);
			clearBuffer.putString(errorMessage.get());
		} else {
			clearBuffer.put(OK);
		}
		clearBuffer.putString(content);
		clearBuffer.flip();
		cipher.doFinal(clearBuffer.getBuffer(), ms.getBuffer());
	}
	
	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		SerializerBuffer clearBuffer = new SerializerBuffer();
		id = ms.getLong();
		ms.getLong();
		cipher.doFinal(ms.getBuffer(), clearBuffer.getBuffer());
		clearBuffer.flip();
		sessionIdentifier = SessionIdentifier.CREATOR.init();
		sessionIdentifier.readFromBuff(clearBuffer);
		filename = clearBuffer.getString();
		byte flag = clearBuffer.get();
		if(flag == ERROR) {
			this.errorMessage = Optional.of(clearBuffer.getString());
		}
		content = clearBuffer.getString();
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		super.writeToBuff(ms);
		ms.putString(content);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		super.readFromBuff(ms);
		this.content = ms.getString();
	}

}
