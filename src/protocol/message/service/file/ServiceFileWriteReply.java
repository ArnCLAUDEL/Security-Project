package protocol.message.service.file;

import protocol.Flag;
import util.Creator;

public class ServiceFileWriteReply extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileWriteReply> CREATOR = ServiceFileWriteReply::new;
	
	private ServiceFileWriteReply() {
		super(Flag.SERVICE_FILE_WRITE_REPLY);
	}

	public ServiceFileWriteReply(String filename) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, filename);
	}
	
	public ServiceFileWriteReply(String filename, String errorMessage) {
		super(Flag.SERVICE_FILE_WRITE_REPLY, filename, errorMessage);
	}

}
