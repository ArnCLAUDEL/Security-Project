package certification.client;

import java.net.SocketAddress;
import java.util.concurrent.Future;

import org.bouncycastle.cert.X509CertificateHolder;

import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

/**
 * {@code ICertificationClientProtocolHandler} interface describes the client-side
 * certification protocol.<br />
 * In this protocol, a client can send {@link AuthRequest} and {@link CertRequest}
 * and should be able to handle incomming {@link AuthReply} and {@link CertReply}.<br />
 * Requests return the result wrapped in a {@link Future}. This {@code Future} will be
 * completed upon reply reception. 
 */
public interface ICertificationClientProtocolHandler {
	
	/**
	 * Sends the {@link AuthRequest} to the given {@link SocketAddress}.<br />
	 * This method returns immediatly with a {@link Future}.
	 * When receiving the result, it will be put into the returned {@link Future}.
	 * @param to The address to send to
	 * @param request The request to send
	 * @return The future that will contain the result
	 */
	Future<X509CertificateHolder> sendAuthRequest(SocketAddress to, AuthRequest request);
	
	/**
	 * Sends the {@link CertRequest} to the given {@link SocketAddress}.<br />
	 * This method returns immediatly with a {@link Future}.
	 * When receiving the result, it will be put into the returned {@link Future}.
	 * @param to The address to send to
	 * @param request The request to send
	 * @return The future that will contain the result
	 */
	Future<X509CertificateHolder> sendCertRequest(SocketAddress to, CertRequest request);
	
	/**
	 * Handles the {@link AuthReply} received from the given {@link SocketAddress}.<br />
	 * This method will update the {@link Future} used when sending the request and
	 * put the result into it.
	 * @param from The reply's sender
	 * @param reply The reply received
	 */
	void handleAuthReply(SocketAddress from, AuthReply reply);
	
	/**
	 * Handles the {@link CertReply} received from the given {@link SocketAddress}.<br />
	 * This method will update the {@link Future} used when sending the request and
	 * put the result into it.
	 * @param from The reply's sender
	 * @param reply The reply received
	 */
	void handleCertReply(SocketAddress from, CertReply reply);
}
