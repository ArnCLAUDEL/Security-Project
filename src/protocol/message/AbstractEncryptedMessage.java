package protocol.message;

public abstract class AbstractEncryptedMessage extends Message implements EncryptedMessage {

	public AbstractEncryptedMessage(byte flag) {
		super(flag);
	}

}
