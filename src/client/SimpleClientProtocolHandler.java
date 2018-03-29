package client;

import java.net.SocketAddress;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.logging.Level;

import certificate.CertificationStorer;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import util.Cheat;

public class SimpleClientProtocolHandler extends AbstractProtocolHandler implements ClientProtocolHandler {

	private final CertificationStorer storer;
	private final SocketAddress caAddress;
	
	public SimpleClientProtocolHandler(NetworkWriter networkWriter, CertificationStorer storer, SocketAddress caAddress) {
		super(networkWriter);
		this.storer = storer;
		this.caAddress = caAddress;
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
		Cheat.LOGGER.log(Level.FINE, "AuthReply handled (not yet implemented)");
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		Cheat.LOGGER.log(Level.FINE, "Handling CertReply");
		try {
			storer.storeCertificate(reply.getAlias(), reply.getCertificateHolder());
		} catch (CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

}
