package protocol.message;

import util.Creator;
import util.SerializerBuffer;

public class AuthRequest extends Message {
	public final static Creator<AuthRequest> CREATOR = AuthRequest::new;
	
	private String filename;
	private String alias;
	
	private AuthRequest() {
		super(Flag.AUTH_REQUEST);
	}
	
	public AuthRequest(String filename, String alias) {
		this();
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
		ms.putString(filename);
		ms.putString(alias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.filename = ms.getString();
		this.alias = ms.getString();
	}

}
