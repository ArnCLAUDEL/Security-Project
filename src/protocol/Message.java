package protocol;

import io.MySerializable;

public abstract class Message implements MySerializable {
	private final byte flag;
	
	public Message(byte flag) {
		this.flag = flag;
	}
	
	public byte getFlag() {
		return flag;
	}
}