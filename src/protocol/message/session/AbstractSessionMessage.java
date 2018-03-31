package protocol.message.session;

import protocol.message.EncryptedMessage;
import protocol.message.TrackedMessage;
import util.MyEncryptedSerializable;

public abstract class AbstractSessionMessage extends EncryptedMessage implements TrackedMessage, MyEncryptedSerializable {

	protected long id;
	
	protected AbstractSessionMessage(byte flag) {	
		super(flag);
	}
	
	protected AbstractSessionMessage(byte flag, long id) {
		this(flag);
		this.id = id;	
	}	
	
	public long getId() {
		return id;
	}
}
