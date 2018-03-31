package session.client.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.NetworkWriter;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import util.Cheat;

public class ConnectedSessionClientProtocolHandler extends ASessionClientProtocolHandler {

	public ConnectedSessionClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}
	
	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request) {
		send(to, request);
	}
	
	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " handled (not yet implemented).");
		
		StringBuilder sb = new StringBuilder();
		sb.append("Session reply :"
				+ "id : " + reply.getId() + "\n"
				+ "destination : " + reply.getDestinationAlias() + "\n"
				+ "secretKey : " + reply.getSecretKey());
		
		Cheat.LOGGER.log(Level.INFO, sb.toString());
	}

}
