package server.ca;

import java.net.SocketAddress;

import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;

public interface CAProtocolHandler {
	void handleAuthRequest(SocketAddress from, AuthRequest request);
	void handleCertRequest(SocketAddress from, CertRequest request);
	void sendAuthReply(SocketAddress to, AuthReply reply);
	void sendCertReply(SocketAddress to, CertReply reply);
}
