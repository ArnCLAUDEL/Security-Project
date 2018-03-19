package protocol;

import io.MySerialisable;

public abstract class Message implements MySerialisable {
	private final byte flag;
	
	public Message(byte flag) {
		this.flag = flag;
	}
	
	public byte getFlag() {
		return flag;
	}
}
