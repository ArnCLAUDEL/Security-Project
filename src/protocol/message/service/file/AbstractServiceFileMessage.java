package protocol.message.service.file;

import java.util.Optional;

import protocol.message.Message;
import util.SerializerBuffer;

public abstract class AbstractServiceFileMessage extends Message {
	protected final static byte OK = 1;
	protected final static byte ERROR = 2;
	
	protected String filename;
	protected Optional<String> errorMessage;
	
	protected AbstractServiceFileMessage(byte flag) {
		super(flag);
		this.errorMessage = Optional.empty();
	}
	
	protected AbstractServiceFileMessage(byte flag, String filename) {
		this(flag);
		this.filename = filename;
	}
	
	protected AbstractServiceFileMessage(byte flag, String filename, String errorMessage) {
		this(flag, filename);
		this.errorMessage = Optional.of(errorMessage);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public Optional<String> getErrorMessage() {
		return errorMessage;
	}
  
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putString(filename);
		if(errorMessage.isPresent()) {
			ms.put(ERROR);
			ms.putString(errorMessage.get());
		} else {
			ms.put(OK);
		}
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.filename = ms.getString();
		byte flag = ms.get();
		if(flag == ERROR) {
			this.errorMessage = Optional.of(ms.getString());
		}
	}
	
	
}
