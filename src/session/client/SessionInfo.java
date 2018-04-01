package session.client;

import java.net.SocketAddress;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.bouncycastle.cert.X509CertificateHolder;

import protocol.Nonce;
import protocol.message.session.SessionRequest;

public class SessionInfo {
	private final long id;
	private final String senderAlias;
	private final String destinationAlias;
	private final SocketAddress destinationAddress;
	private final X509CertificateHolder holder;
	private final Nonce senderNonce;
	
	private Optional<Nonce> destinationNonce;
	private Optional<SecretKey> secretKey;
	
	public SessionInfo(SessionRequest request, SocketAddress destinationAddress, X509CertificateHolder holder) {
		this(request.getId(), request.getSenderAlias(), request.getDestinationAlias(), request.getSenderNonce(), destinationAddress, holder);
	}
	
	// TODO
	public SessionInfo(long id, String senderAlias) {
		this(id, senderAlias, null, null, null, null);
	}
	
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
	
	public boolean setSecretKey(SecretKey secretKey) {
		if(this.secretKey.isPresent())
			return false;
		this.secretKey = Optional.of(secretKey);
		return true;
	}
	
	public Optional<Nonce> getDestinationNonce() {
		return destinationNonce;
	}
	
	public boolean setDestinationNonce(Nonce nonce) {
		if(destinationNonce.isPresent())
			return false;
		destinationNonce = Optional.of(nonce);
		return true;
	}
	
	public boolean isValid() {
		return destinationNonce.isPresent();
	}
	
}
