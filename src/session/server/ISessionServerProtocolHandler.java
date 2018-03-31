package session.server;

import java.net.SocketAddress;

import javax.crypto.Cipher;

import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;

public interface ISessionServerProtocolHandler {
	void handleSessionRequest(SocketAddress from, SessionRequest request);
	void sendSessionreply(SocketAddress to, SessionReply reply, Cipher cipher);
}
