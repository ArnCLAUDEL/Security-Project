package certification;

import java.net.SocketAddress;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.logging.Level;

import protocol.NetworkWriter;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import util.Cheat;

public class ConnectedCertificationProtocolHandler extends AbstractCertificationProtocolHandler implements CertificationProtocolHandler {
	
	public ConnectedCertificationProtocolHandler(NetworkWriter networkWriter, CertificationStorer storer) {
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
