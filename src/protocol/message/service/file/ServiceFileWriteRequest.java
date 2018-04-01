package protocol.message.service.file;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import protocol.Flag;
import session.client.SessionIdentifier;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileWriteRequest extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileWriteRequest> CREATOR = ServiceFileWriteRequest::new;
	
	private String content;
	
	private ServiceFileWriteRequest() {
		super(Flag.SERVICE_FILE_WRITE_REQUEST);
	}
	
	public ServiceFileWriteRequest(long id, String filename, String content, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_WRITE_REQUEST, id, filename, sessionIdentifier);
		this.content = content;
	}
	
	public ServiceFileWriteRequest(String filename, long id, String content, String errorMessage, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_WRITE_REQUEST, id, errorMessage, sessionIdentifier);
		this.content = content;
	}
	
	public String getContent() {
		return content;
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

	@Override
	public void writeToBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFromBuff(SerializerBuffer ms, Cipher cipher) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated method stub
		
	}

}
