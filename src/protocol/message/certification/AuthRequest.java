package protocol.message.certification;

import protocol.Flag;
import util.Creator;
import util.SerializerBuffer;

public class AuthRequest extends AbstractCertificationMessage {
	public final static Creator<AuthRequest> CREATOR = AuthRequest::new;
	
	private String filename;
	private String alias;
	
	private AuthRequest() {
		super(Flag.AUTH_REQUEST);
	}
	
	public AuthRequest(long id, String filename, String alias) {
		super(Flag.AUTH_REQUEST, id);
		this.filename = filename;
		this.alias = alias;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getAlias() {
		return alias;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		ms.putString(filename);
		ms.putString(alias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.id = ms.getLong();
		this.filename = ms.getString();
		this.alias = ms.getString();
	}

}
