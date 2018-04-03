package certification.client.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

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
	
	public ConnectedCertificationClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer, PublicKey certificationServerPublicKey) {
		super(networkWriter, storer, certificationServerPublicKey);
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
			Signature verifier = Signature.getInstance("SHA1withRSA");
			verifier.initVerify(new JcaX509CertificateConverter().getCertificate(storer.getCertificate("root-certification-authority")));

			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
			results.get(reply.getId()).complete(reply.getCertificateHolder());
			if(verifier.verify(reply.getCertificateHolder().getSignature())) {
				Cheat.LOGGER.log(Level.WARNING, "Signature verified.");
			} else {
				Cheat.LOGGER.log(Level.WARNING, "Incorrect certificate signature.");
			}
		} catch (CertificateException | KeyStoreException | SignatureException | InvalidKeyException | NoSuchAlgorithmException | NoSuchElementException | IOException e) {
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
