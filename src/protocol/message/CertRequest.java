package protocol.message;

import util.Creator;
import util.SerializerBuffer;

public class CertRequest extends Message {
	public final static Creator<CertRequest> CREATOR = CertRequest::new;
	
	private String alias;
	
	private CertRequest() {
		super(Flag.CERT_REQUEST);
	}
	
	public CertRequest(String alias) {
		this();
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putString(alias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.alias = ms.getString();
	}

}
