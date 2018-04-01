package certification.client;

import java.net.SocketAddress;
import java.util.concurrent.Future;

import org.bouncycastle.cert.X509CertificateHolder;

import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

public interface ICertificationClientProtocolHandler {
	Future<X509CertificateHolder> sendAuthRequest(SocketAddress to, AuthRequest request);
	Future<X509CertificateHolder> sendCertRequest(SocketAddress to, CertRequest request);
	void handleAuthReply(SocketAddress from, AuthReply reply);
	void handleCertReply(SocketAddress from, CertReply reply);
}
