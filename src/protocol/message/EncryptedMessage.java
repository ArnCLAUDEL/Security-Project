package protocol.message;

import util.MyEncryptedSerializable;

public interface EncryptedMessage extends MyEncryptedSerializable {
	byte getFlag();
}
