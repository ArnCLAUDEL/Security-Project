package certification.server;

import java.net.SocketAddress;

import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

public interface ICertificationServerProtocolHandler {
	void handleAuthRequest(SocketAddress from, AuthRequest request);
	void handleCertRequest(SocketAddress from, CertRequest request);
	void sendAuthReply(SocketAddress to, AuthReply reply);
	void sendCertReply(SocketAddress to, CertReply reply);
}
