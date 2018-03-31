package protocol.message.service.file;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileReadRequest extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileReadRequest> CREATOR = ServiceFileReadRequest::new;
	
	private ServiceFileReadRequest() {
		super(Flag.SERVICE_FILE_READ_REQUEST);
	}
	
	public ServiceFileReadRequest(long id, String filename) {
		super(Flag.SERVICE_FILE_READ_REQUEST, id, filename);
	}
	
	public ServiceFileReadRequest(long id, String filename, String errorMessage) {
		super(Flag.SERVICE_FILE_READ_REQUEST, id, filename, errorMessage);
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
