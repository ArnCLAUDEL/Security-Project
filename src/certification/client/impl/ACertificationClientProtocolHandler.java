package certification.client.impl;

import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.crypto.Cipher;

import org.bouncycastle.cert.X509CertificateHolder;

import certification.ICertificationStorer;
import certification.client.ICertificationClientProtocolHandler;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import util.Cheat;

public class ACertificationClientProtocolHandler extends AbstractProtocolHandler implements ICertificationClientProtocolHandler {
	protected final ICertificationStorer storer;
	protected final PublicKey certificationServerPublicKey;
	
	public ACertificationClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer, PublicKey certificationServerPublicKey) {
		super(networkWriter);
		this.storer = storer;
		this.certificationServerPublicKey = certificationServerPublicKey;
	}
	
	@Override
	public Future<X509CertificateHolder> sendAuthRequest(SocketAddress to, AuthRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public Future<X509CertificateHolder> sendCertRequest(SocketAddress to, CertRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}
}
