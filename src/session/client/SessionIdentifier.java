package session.client;

import io.MySerializable;
import protocol.Nonce;
import session.client.impl.SessionManager;
import util.Creator;
import util.SerializerBuffer;


/**
 * {@code SessionIdentifier} represents a session with only two informations 
 * : the id and the associated {@link Nonce}.<br />
 * This object can be used to avoid sending an entire {@link SessionInfo} in
 * a session message. This can be used retrieved in a local {@link SessionManager}
 * the session key to decrypt an encrypted session message.
 */
public class SessionIdentifier implements MySerializable {
	public final static Creator<SessionIdentifier> CREATOR = SessionIdentifier::new;
	
	/**
	 * The session id
	 */
	private long id;
	
	/**
	 * The session nonce
	 */
	private Nonce nonce;
	
	private SessionIdentifier() {
		
	}
	
	/**
	 * Creates a new instance with the given session id and nonce.
	 * @param id The session id
	 * @param nonce The session nonce
	 */
	public SessionIdentifier(long id, Nonce nonce) {
		this.id = id;
		this.nonce = nonce;
	}
	
	/**
	 * Creates a new instance using informations contained in the given
	 * {@link SessionInfo}.<br />
	 * <tt>Note</tt> that the {@code SessionInfo} must represents a valid session
	 * and therefore return <tt>true</tt> when calling <tt>isValid()</tt>.
	 * @param info The valid session
	 * @throws IllegalArgumentException If the given {@code SessionInfo} is not validated
	 */
	public SessionIdentifier(SessionInfo info) throws IllegalArgumentException {
		if(!info.isValid())
			throw new IllegalArgumentException("Expecting a validated SessionInfo.");
		
		this.id = info.getId();
		this.nonce = info.getDestinationNonce().get();
	}
	
	/**
	 * Returns the session id.
	 * @return The session id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns the session {@link Nonce}.
	 * @return The session nonce
	 */
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
