package session.client;

import io.MySerializable;
import protocol.Nonce;
import util.Creator;
import util.SerializerBuffer;

public class SessionIdentifier implements MySerializable {
	public final static Creator<SessionIdentifier> CREATOR = SessionIdentifier::new;
	
	private long id;
	private Nonce nonce;
	
	private SessionIdentifier() {
		
	}
	
	public SessionIdentifier(long id, Nonce nonce) {
		this.id = id;
		this.nonce = nonce;
	}
	
	public SessionIdentifier(SessionInfo info) {
		if(!info.isValid())
			throw new IllegalArgumentException("Expecting a validated SessionInfo.");
		
		this.id = info.getId();
		this.nonce = info.getDestinationNonce().get();
	}
	
	public long getId() {
		return id;
	}
	
	public Nonce getNonce() {
		return nonce;
	}

	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
		nonce.writeToBuff(ms);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		id = ms.getLong();
		nonce = Nonce.CREATOR.init();
		nonce.readFromBuff(ms);
	}
	
	
}
