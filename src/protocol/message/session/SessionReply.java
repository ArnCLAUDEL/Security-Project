package protocol.message.session;

import protocol.Flag;
import protocol.message.Message;
import util.SerializerBuffer;

public class SessionReply extends Message {

	private SessionReply() {
		super(Flag.SESSION_REPLY);
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
