package protocol.message.service.file;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class ServiceFileReadReply extends AbstractServiceFileMessage {
	public final static Creator<ServiceFileReadReply> CREATOR = ServiceFileReadReply::new;
	
	private String content;
	
	private ServiceFileReadReply() {
		super(Flag.SERVICE_FILE_READ_REPLY);
	}
	
	public ServiceFileReadReply(String filename, String content, long id) {
		super(Flag.SERVICE_FILE_READ_REPLY, id, filename);
		this.content = content;
	}
	
	public ServiceFileReadReply(long id, String filename, String content, String errorMessage) {
		super(Flag.SERVICE_FILE_READ_REPLY, id, filename, errorMessage);
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
