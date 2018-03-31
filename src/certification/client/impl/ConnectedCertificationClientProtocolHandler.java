package certification.client.impl;

import java.net.SocketAddress;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.logging.Level;

import certification.ICertificationStorer;
import certification.client.ICertificationClientProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import util.Cheat;

public class ConnectedCertificationClientProtocolHandler extends ACertificationClientProtocolHandler implements ICertificationClientProtocolHandler {
	
	public ConnectedCertificationClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer) {
		super(networkWriter, storer);
	}

	@Override
	public void sendAuthRequest(SocketAddress to, AuthRequest request) {
		send(to, request);
	}

	@Override
	public void sendCertRequest(SocketAddress to, CertRequest request) {
		send(to, request);
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		Cheat.LOGGER.log(Level.FINE, "Handling AuthReply..");
		try {
			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
		} catch (CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		Cheat.LOGGER.log(Level.FINE, "Handling CertReply..");
		try {
			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
		} catch (CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

}
