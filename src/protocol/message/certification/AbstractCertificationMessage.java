package protocol.message.certification;

import protocol.message.Message;
import protocol.message.TrackedMessage;

public abstract class AbstractCertificationMessage extends Message implements TrackedMessage {

	protected long id;
	
	protected AbstractCertificationMessage(byte flag) {	
		super(flag);
	}
	
	protected AbstractCertificationMessage(byte flag, long id) {
		this(flag);
		this.id = id;	
	}	
	
	public long getId() {
		return id;
	}

}
