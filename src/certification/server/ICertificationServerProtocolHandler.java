package certification.server;

import java.net.SocketAddress;

import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

/**
 * {@code ICertificationServerProtocolHandler} interface describes the server-side
 * certification protocol.<br />
 * In this protocol, a server should be able to handle incomming {@link AuthRequest} and {@link CertRequest}
 * and can send {@link AuthReply} and {@link CertReply}.<br /> 
 */
public interface ICertificationServerProtocolHandler {
	
	/**
	 * Handles the {@link AuthRequest} from the given {@link SocketAddress}.<br />
	 * This will then generate and send a {@link AuthReply} to the sender.<br />
	 * <tt>Note</tt> that if an error occurs, the server could choose not to reply.
	 * @param from The request's sender
	 * @param request The request received
	 */
	void handleAuthRequest(SocketAddress from, AuthRequest request);
	
	/**
	 * Handles the {@link CertRequest} from the given {@link SocketAddress}.<br />
	 * This will then generate and send a {@link CertReply} to the sender.<br />
	 * * <tt>Note</tt> that if an error occurs, the server could choose not to reply.
	 * @param from The request's sender
	 * @param request The request received
	 */
	void handleCertRequest(SocketAddress from, CertRequest request);
	
	/**
	 * Sends the {@link AuthReply} to the given {@link SocketAddress}.<br />
	 * @param to The address to send to
	 * @param reply The reply to send
	 */
	void sendAuthReply(SocketAddress to, AuthReply reply);
	
	/**
	 * Sends the {@link CertReply} to the given {@link SocketAddress}.<br />
	 * @param to The address to send to
	 * @param reply The reply to send
	 */
	void sendCertReply(SocketAddress to, CertReply reply);
}
