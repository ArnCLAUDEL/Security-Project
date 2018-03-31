package session.client.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import javax.crypto.Cipher;

import certification.impl.CertificationStorer;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.ISessionClientProtocolHandler;
import session.client.SessionInfo;
import util.Cheat;

public abstract class ASessionClientProtocolHandler extends AbstractProtocolHandler implements ISessionClientProtocolHandler {

	protected final CertificationStorer storer;
	
	public ASessionClientProtocolHandler(NetworkWriter networkWriter, CertificationStorer storer) {
		super(networkWriter);
		this.storer = storer;
	}

	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	@Override
	public void sendSessionInit(SocketAddress to, SessionInit init, Cipher cipher) {
		Cheat.LOGGER.log(Level.FINEST, init + " ignored.");
		
	}

	@Override
	public void handleSessionInit(SocketAddress from, SessionInit init) {
		Cheat.LOGGER.log(Level.FINEST, init + " ignored.");
	}

	@Override
	public void sendSessionAck(SocketAddress to, SessionAck ack, Cipher cipher) {
		Cheat.LOGGER.log(Level.FINEST, ack + " ignored.");
	}

	@Override
	public void handleSessionAck(SocketAddress from, SessionAck ack) {
		Cheat.LOGGER.log(Level.FINEST, ack + " ignored.");
	}

	@Override
	public void sendSessionOk(SocketAddress to, SessionOk ok, Cipher cipher) {
		Cheat.LOGGER.log(Level.FINEST, ok + " ignored.");
	}

	@Override
	public void handleSessionOk(SocketAddress from, SessionOk ok) {
		Cheat.LOGGER.log(Level.FINEST, ok + " ignored.");
	}

}
