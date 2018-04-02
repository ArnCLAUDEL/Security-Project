package session.client;

import java.net.SocketAddress;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.bouncycastle.cert.X509CertificateHolder;

import protocol.Nonce;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionRequest;
import session.client.impl.SessionManager;

/**
 * {@code SessionInfo} represents a session with all the informations needed
 * to handle the protocol.<br />
 * This object should be created when a new session is made and saved in a
 * {@link SessionManager}. This object should be updated when receiving a new
 * session protocol message.<br />
 * The session represented by this object is <tt>valid</tt> if the second
 * {@link Nonce} is present.
 */
public class SessionInfo {
	
	/**
	 * The session id
	 */
	private final long id;
	
	/**
	 * The alias of the initial client
	 */
	private final String senderAlias;
	
	/**
	 * The alias of the other client
	 */
	private final String destinationAlias;
	
	/**
	 * The address of the other client
	 */
	private final SocketAddress destinationAddress;
	
	/**
	 * The certificate of the other client
	 */
	private final X509CertificateHolder holder;
	
	/**
	 * The first nonce sent to the server
	 */
	private final Nonce senderNonce;
	
	/**
	 * The second nonce sent by the other client
	 */
	private Optional<Nonce> destinationNonce;
	
	/**
	 * The session key
	 */
	private Optional<SecretKey> secretKey;
	
	/**
	 * Creates a new instance by retrieving some informations from the given {@link SessionRequest}.
	 * @param request The request to retrieve the infromations from 
	 * @param destinationAddress The address of the other client
	 * @param holder The certificate of the other client
	 */
	public SessionInfo(SessionRequest request, SocketAddress destinationAddress, X509CertificateHolder holder) {
		this(request.getId(), request.getSenderAlias(), request.getDestinationAlias(), request.getSenderNonce(), destinationAddress, holder);
	}
	
	/**
	 * Creates a new instance with only an id and the alias of the intial client.<br />
	 * This should be used only by the other client, when receiving a {@link SessionInit}.
	 * @param id The sesion id
	 * @param senderAlias The alias of the initial client
	 */
	public SessionInfo(long id, String senderAlias) {
		this(id, senderAlias, null, null, null, null);
	}
	
	/**
	 * Creates a new instance with the given parameters. The session key and the second nonce
	 * are empty.
	 * @param id The session id
	 * @param senderAlias The alias of the initial client
	 * @param destinationAlias The alias of the other client
	 * @param senderNonce The firt nonce sent to the server
	 * @param destinationAddress The address of the other client
	 * @param holder The certificate of the other client
	 */
	public SessionInfo(long id, String senderAlias, String destinationAlias, Nonce senderNonce, SocketAddress destinationAddress, X509CertificateHolder holder) {
		super();
		this.id = id;
		this.senderAlias = senderAlias;
		this.destinationAlias = destinationAlias;
		this.destinationAddress = destinationAddress;
		this.senderNonce = senderNonce;
		this.holder = holder;
		this.secretKey = Optional.empty();
		this.destinationNonce = Optional.empty();
	}
	
	
	public long getId() {
		return id;
	}
	
	public String getSenderAlias() {
		return senderAlias;
	}
	
	public String getDestinationAlias() {
		return destinationAlias;
	}
	
	public SocketAddress getDestinationAddress() {
		return destinationAddress;
	}
	
	public Nonce getSenderNonce() {
		return senderNonce;
	}
	
	public X509CertificateHolder getCertificateHolder() {
		return holder;
	}
	
	public Optional<SecretKey> getSecretKey() {
		return secretKey;
	}
	
	/**
	 * Sets the session key if not already set.
	 * @param secretKey The session key
	 * @return <tt>true</tt> if there was not already a session key
	 */
	public boolean setSecretKey(SecretKey secretKey) {
		if(this.secretKey.isPresent())
			return false;
		this.secretKey = Optional.of(secretKey);
		return true;
	}
	
	public Optional<Nonce> getDestinationNonce() {
		return destinationNonce;
	}
	
	/**
	 * Sets the seconde nonce if not already set.
	 * @param nonce The second nonce sent by the other client
	 * @return <tt>true</tt> if there was not already a second nonce
	 */
	public boolean setDestinationNonce(Nonce nonce) {
		if(destinationNonce.isPresent())
			return false;
		destinationNonce = Optional.of(nonce);
		return true;
	}
	
	/**
	 * Verifies if the session represented by this object is valid.<br />
	 * A session is valid only if the second nonce sent by the other client is present. 
	 * @return <tt>true</tt> if the session is valid.
	 */
	public boolean isValid() {
		return destinationNonce.isPresent();
	}
	
}
