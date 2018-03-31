package certification.client.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

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
	
	public ACertificationClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer) {
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
