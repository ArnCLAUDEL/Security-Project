package certification.client;

import java.net.SocketAddress;

import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

public interface ICertificationClientProtocolHandler {
	void sendAuthRequest(SocketAddress to, AuthRequest request);
	void sendCertRequest(SocketAddress to, CertRequest request);
	void handleAuthReply(SocketAddress from, AuthReply reply);
	void handleCertReply(SocketAddress from, CertReply reply);
}
