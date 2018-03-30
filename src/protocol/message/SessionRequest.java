package protocol.message;

import protocol.Flag;
import util.SerializerBuffer;

public class SessionRequest extends Message {

	private SessionRequest() {
		super(Flag.SESSION_REQUEST);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		// TODO Auto-generated method stub
		
	}

}
