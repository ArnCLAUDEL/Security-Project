package session.client;

import java.net.SocketAddress;

import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;

public interface ISessionClientProtocolHandler {
	void sendSessionRequest(SocketAddress to, SessionRequest request);
	void handleSessionReply(SocketAddress from, SessionReply reply);
}
