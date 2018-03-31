package session.client;

import java.net.SocketAddress;

import javax.crypto.Cipher;

import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;

public interface ISessionClientProtocolHandler {
	void sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info);
	void handleSessionReply(SocketAddress from, SessionReply reply);
	void sendSessionInit(SocketAddress to, SessionInit init, Cipher cipher);
	void handleSessionInit(SocketAddress from, SessionInit init);
	void sendSessionAck(SocketAddress to, SessionAck ack, Cipher cipher);
	void handleSessionAck(SocketAddress from, SessionAck ack);
	void sendSessionOk(SocketAddress to, SessionOk ok, Cipher cipher);
	void handleSessionOk(SocketAddress from, SessionOk ok);
}
