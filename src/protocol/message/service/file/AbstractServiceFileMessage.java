package protocol.message.service.file;

import java.util.Optional;

import protocol.message.AbstractEncryptedMessage;
import session.client.SessionIdentifier;
import util.SerializerBuffer;

public abstract class AbstractServiceFileMessage extends AbstractEncryptedMessage {
	protected final static byte OK = 1;
	protected final static byte ERROR = 2;
	
	protected long id;
	protected SessionIdentifier sessionIdentifier;
	protected String filename;
	protected Optional<String> errorMessage;
		
	protected AbstractServiceFileMessage(byte flag) {
		super(flag);
		this.errorMessage = Optional.empty();
	}
	
	protected AbstractServiceFileMessage(byte flag, long id, SessionIdentifier sessionIdentifier) {
		super(flag);
		this.id = id;
		this.sessionIdentifier = sessionIdentifier;
		this.errorMessage = Optional.empty();
	}
	
	protected AbstractServiceFileMessage(byte flag, long id, String filename, SessionIdentifier sessionIdentifier) {
		this(flag);
		this.id = id;
		this.sessionIdentifier = sessionIdentifier;
		this.filename = filename;
	}
	
	protected AbstractServiceFileMessage(byte flag, long id, String filename, String errorMessage, SessionIdentifier sessionIdentifier) {
		this(flag, id, filename, sessionIdentifier);
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
	
	public SessionIdentifier getSessionIdentifier() {
		return sessionIdentifier;
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
		sessionIdentifier.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.id = ms.getLong();
		this.filename = ms.getString();
		byte flag = ms.get();
		if(flag == ERROR) {
			this.errorMessage = Optional.of(ms.getString());
		}
		this.sessionIdentifier = SessionIdentifier.CREATOR.init();
		this.sessionIdentifier.readFromBuff(ms);
	}
	
	
}
