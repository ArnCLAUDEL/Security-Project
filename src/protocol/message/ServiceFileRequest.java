package protocol.message;

import util.SerializerBuffer;

public class ServiceFileRequest extends Message {

	private ServiceFileRequest() {
		super(Flag.SERVICE_FILE_REQUEST);
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
