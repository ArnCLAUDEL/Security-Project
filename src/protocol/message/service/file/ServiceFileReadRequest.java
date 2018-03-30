package protocol.message.service.file;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileReadRequest extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileReadRequest> CREATOR = ServiceFileReadRequest::new;
	
	private ServiceFileReadRequest() {
		super(Flag.SERVICE_FILE_READ_REQUEST);
	}
	
	public ServiceFileReadRequest(String filename) {
		super(Flag.SERVICE_FILE_READ_REQUEST, filename);
	}
	
	public ServiceFileReadRequest(String filename, String errorMessage) {
		super(Flag.SERVICE_FILE_READ_REQUEST, filename, errorMessage);
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		super.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		super.readFromBuff(ms);
	}

}
