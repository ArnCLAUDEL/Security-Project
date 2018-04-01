package protocol.message.certification;

import protocol.Flag;
import protocol.message.TrackedMessage;
import util.Creator;
import util.SerializerBuffer;

public class CertRequest extends AbstractCertificationMessage implements TrackedMessage {
	public final static Creator<CertRequest> CREATOR = CertRequest::new;
	private String alias;
	
	private CertRequest() {
		super(Flag.CERT_REQUEST);
	}
	
	public CertRequest(long id, String alias) {
		super(Flag.CERT_REQUEST, id);
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		ms.putString(alias);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.id = ms.getLong();
		this.alias = ms.getString();
	}

}
