package protocol.message;

import util.Creator;
import util.SerializerBuffer;

public class AuthReply extends Message {
	public final static Creator<AuthReply> CREATOR = AuthReply::new;
	
	public AuthReply() {
		super(Flag.AUTH_REPLY);
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
