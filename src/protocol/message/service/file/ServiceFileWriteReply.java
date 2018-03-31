package protocol.message.service.file;

import protocol.Flag;
import util.Creator;

public class ServiceFileWriteReply extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileWriteReply> CREATOR = ServiceFileWriteReply::new;
	
	private ServiceFileWriteReply() {
		super(Flag.SERVICE_FILE_WRITE_REPLY);
	}

	public ServiceFileWriteReply(long id, String filename) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, id, filename);
	}
	
	public ServiceFileWriteReply(long id, String filename, String errorMessage) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, id, filename, errorMessage);
	}

}
