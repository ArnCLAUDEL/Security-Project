package certification.client.impl;

import java.net.SocketAddress;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.bouncycastle.cert.X509CertificateHolder;

import certification.ICertificationStorer;
import certification.client.ICertificationClientProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import util.Cheat;

public class ConnectedCertificationClientProtocolHandler extends ACertificationClientProtocolHandler implements ICertificationClientProtocolHandler {
	
	private final Map<Long, CompletableFuture<X509CertificateHolder>> results;
	
	public ConnectedCertificationClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer) {
		super(networkWriter, storer);
		this.results = new TreeMap<>();
	}

	@Override
	public Future<X509CertificateHolder> sendAuthRequest(SocketAddress to, AuthRequest request) {
		CompletableFuture<X509CertificateHolder> result = new CompletableFuture<>();	
		results.put(request.getId(), result);
		send(to, request);
		return result;
	}

	@Override
	public Future<X509CertificateHolder> sendCertRequest(SocketAddress to, CertRequest request) {
		CompletableFuture<X509CertificateHolder> result = new CompletableFuture<>();	
		results.put(request.getId(), result);
		send(to, request);
		return result;
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		Cheat.LOGGER.log(Level.FINE, "Handling AuthReply..");
		try {
			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
			results.get(reply.getId()).complete(reply.getCertificateHolder());
		} catch (CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		Cheat.LOGGER.log(Level.FINE, "Handling CertReply..");
		try {
			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
			results.get(reply.getId()).complete(reply.getCertificateHolder());
		} catch (CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

}
