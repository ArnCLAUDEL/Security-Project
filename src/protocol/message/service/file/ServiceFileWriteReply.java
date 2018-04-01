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

public class ServiceFileWriteReply extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileWriteReply> CREATOR = ServiceFileWriteReply::new;
	
	private ServiceFileWriteReply() {
		super(Flag.SERVICE_FILE_WRITE_REPLY);
	}

	public ServiceFileWriteReply(long id, String filename, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, id, filename, sessionIdentifier);
	}
	
	public ServiceFileWriteReply(long id, String filename, String errorMessage, SessionIdentifier sessionIdentifier) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, id, filename, errorMessage, sessionIdentifier);
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
