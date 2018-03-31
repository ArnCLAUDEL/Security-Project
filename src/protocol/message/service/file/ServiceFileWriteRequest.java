package protocol.message.service.file;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileWriteRequest extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileWriteRequest> CREATOR = ServiceFileWriteRequest::new;
	
	private String content;
	
	private ServiceFileWriteRequest() {
		super(Flag.SERVICE_FILE_WRITE_REQUEST);
	}
	
	public ServiceFileWriteRequest(long id, String filename, String content) {
		super(Flag.SERVICE_FILE_WRITE_REQUEST, id, filename);
		this.content = content;
	}
	
	public ServiceFileWriteRequest(String filename, long id, String content, String errorMessage) {
		super(Flag.SERVICE_FILE_WRITE_REQUEST, id, errorMessage);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		super.writeToBuff(ms);
		ms.putString(content);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		super.readFromBuff(ms);
		this.content = ms.getString();
	}

}
