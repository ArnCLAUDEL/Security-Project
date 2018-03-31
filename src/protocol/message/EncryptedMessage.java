package protocol.message;

import util.MyEncryptedSerializable;

public abstract class EncryptedMessage extends Message implements MyEncryptedSerializable {

	public EncryptedMessage(byte flag) {
		super(flag);
	}

}
