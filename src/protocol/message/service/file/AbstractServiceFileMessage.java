package protocol.message.service.file;

import java.util.Optional;

import protocol.message.Message;
import util.SerializerBuffer;

public abstract class AbstractServiceFileMessage extends Message {
	protected final static byte OK = 1;
	protected final static byte ERROR = 2;
	
	protected long id;
	protected String filename;
	protected Optional<String> errorMessage;
		
	protected AbstractServiceFileMessage(byte flag) {
		super(flag);
		this.errorMessage = Optional.empty();
	}
	
	protected AbstractServiceFileMessage(byte flag, long id) {
		super(flag);
		this.id = id;
		this.errorMessage = Optional.empty();
	}
	
	protected AbstractServiceFileMessage(byte flag, long id, String filename) {
		this(flag);
		this.id = id;
		this.filename = filename;
	}
	
	protected AbstractServiceFileMessage(byte flag, long id, String filename, String errorMessage) {
		this(flag, id, filename);
		this.errorMessage = Optional.of(errorMessage);
	}
	
	public long getId() {
		return id;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public Optional<String> getErrorMessage() {
		return errorMessage;
	}
  
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
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
		this.id = ms.getLong();
		this.filename = ms.getString();
		byte flag = ms.get();
		if(flag == ERROR) {
			this.errorMessage = Optional.of(ms.getString());
		}
	}
	
	
}
