package session.server;

import java.net.SocketAddress;

import javax.crypto.Cipher;

import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;

/**
 * {@code ISessionServerProtocolHandler} interface describes the server-side
 * session protocol.<br />
 * In this protocol, a server should be able to handle incomming {@link SessionRequest}
 * and can send {@link SessionReply}. The reply should be encrypted with the sender's
 * public key.
 */
public interface ISessionServerProtocolHandler {
	
	/**
	 * Handles the {@link SessionRequest} from the given {@link SocketAddress}.<br />
	 * The server will then generate a new session key and build a send a
	 * {@link SessionReply} if no error occurs. 
	 * @param from The request's sender
	 * @param request The request received
	 */
	void handleSessionRequest(SocketAddress from, SessionRequest request);
	
	/**
	 * Sends the {@link SessionReply}, encrypted with the given {@link Cipher},
	 * to the given {@link SocketAddress}.<br />
	 * The provided {@code Cipher} <tt>must</tt> have been initialized in 
	 * {@code Cipher.PUBLIC_KEY} in order to be able to encrypt the message.
	 * @param to The address to send to
	 * @param reply The reply to encrypt and send
	 * @param cipher The cipher to use for encryption
	 */
	void sendSessionreply(SocketAddress to, SessionReply reply, Cipher cipher);
}
