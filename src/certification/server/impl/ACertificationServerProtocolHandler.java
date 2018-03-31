package certification.server.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import certification.server.ICertificationServer;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import util.Cheat;

public abstract class ACertificationServerProtocolHandler extends AbstractProtocolHandler implements ICertificationServer {

	public ACertificationServerProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}

	@Override
	public void handleAuthRequest(SocketAddress from, AuthRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void handleCertRequest(SocketAddress from, CertRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void sendAuthReply(SocketAddress to, AuthReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	@Override
	public void sendCertReply(SocketAddress to, CertReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

}
