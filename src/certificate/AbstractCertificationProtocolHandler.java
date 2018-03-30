package certificate;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import util.Cheat;

public class AbstractCertificationProtocolHandler extends AbstractProtocolHandler implements CertificationProtocolHandler {
	protected final CertificationStorer storer;
	
	public AbstractCertificationProtocolHandler(NetworkWriter networkWriter, CertificationStorer storer) {
		super(networkWriter);
		this.storer = storer;
	}
	
	@Override
	public void sendAuthRequest(SocketAddress to, AuthRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void sendCertRequest(SocketAddress to, CertRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
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
