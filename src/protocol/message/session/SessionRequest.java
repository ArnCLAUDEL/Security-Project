package protocol.message.session;

import protocol.Flag;
import protocol.message.Message;
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
