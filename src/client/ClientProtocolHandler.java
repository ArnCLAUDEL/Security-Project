package client;

import java.net.SocketAddress;

import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;

public interface ClientProtocolHandler {
	void sendAuthRequest(SocketAddress to, AuthRequest request);
	void sendCertRequest(SocketAddress to, CertRequest request);
	void handleAuthReply(SocketAddress from, AuthReply reply);
	void handleCertReply(SocketAddress from, CertReply reply);
}
