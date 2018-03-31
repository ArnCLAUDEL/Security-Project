package session.client.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.ISessionClientProtocolHandler;
import util.Cheat;

public abstract class ASessionClientProtocolHandler extends AbstractProtocolHandler implements ISessionClientProtocolHandler {

	public ASessionClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}

	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

}
