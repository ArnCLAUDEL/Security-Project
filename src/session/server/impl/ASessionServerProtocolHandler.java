package session.server.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import javax.crypto.Cipher;

import certification.ICertificationStorer;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.server.ISessionServerProtocolHandler;
import session.server.ISessionProvider;
import util.Cheat;

public class ASessionServerProtocolHandler extends AbstractProtocolHandler implements ISessionServerProtocolHandler {

	protected final ISessionProvider provider;
	protected final ICertificationStorer storer;
	
	public ASessionServerProtocolHandler(NetworkWriter networkWriter, ISessionProvider provider, ICertificationStorer storer) {
		super(networkWriter);
		this.provider = provider;
		this.storer = storer;
	}

	@Override
	public void handleSessionRequest(SocketAddress from, SessionRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void sendSessionreply(SocketAddress to, SessionReply reply, Cipher cipher) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

}
