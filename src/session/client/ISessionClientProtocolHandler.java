package session.client;

import java.net.SocketAddress;
import java.util.concurrent.Future;

import javax.crypto.Cipher;

import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.impl.SessionManager;

/**
 * {@code ISessionClientProtocolHandler} interface describes the client-side
 * session protocol.<br />
 * In this protocol, a client can send {@link SessionRequest}, {@link SessionInit}
 * {@link SessionAck}, {@link SessionOk} and should be able to handle {@link SessionReply}, {@code SessionInit}
 * {@code SessionAck} and {@code SessionOk}.
 * Any client can start a new session with another client but they will need a server to handle the first 
 * {@code SessionRequest}.<br /><br />
 * A new session can be created by following these operations :<br />
 * A client sends a {@code SessionRequest} (that contains various informations about both clients) to session server.<br />
 * The server then generates a {@code SessionReply}, encrypted with the sender's public key, that contains the session
 * key and the encrypted messaged to forward to the other client.<br />
 * The sender client now can send a {@code SessionInit} to the other one, using its public key to encrypt the message.<br />
 * When receiving a {@code SessionInit}, a {@link Nonce} should be generated and associated to this session. 
 * This nonce is then attached to the {@code SessionAck} and send to the initial client using the session key for encryption.<br />
 * When receiving the message with the nonce, the initial client should retrieve the nonce and do a small computation that is
 * easy for the nonce-owner but hard for others. A new {@code Nonce} is generated and is sent in a {@code SessionOk} and it completes
 * the session generation for this client. <br />
 * When receiving the new generated {@code Nonce}, the nonce is verified and if valid, the session generation is completed on this client as well.   
 */
public interface ISessionClientProtocolHandler {
	
	/**
	 * Sends the {@link SessionRequest} to the given {@link SocketAddress} and saves this new session
	 * in a {@link SessionManager}.<br />
	 * The given address should be an address to a session server.<br />
	 * The given {@link SessionInfo} will be used to save this new session to the {@code SessionManager}.<br />
	 * This method returns immediatly a {@link SessionIdentifier} that will be completed upon receiving a
	 * {@link SessionAck}.
	 * @param to The session server address to send to
	 * @param request The request to send
	 * @param info The info used to save this request in the session manager
	 * @return A future that will contains the identifiers of this new session 
	 */
	Future<SessionIdentifier> sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info);
	
	/**
	 * Handles the {@link SessionReply} from the given {@link SocketAddress} and generates a {@link SessionInit}.<br />
	 * This method builds a new {@code SessionInit}, retrieves the {@link SessionInfo} in the {@link SessionManager} 
	 * to get the certificate and the address of the other client. The {@link Cipher} is made with its public key
	 * and <tt>sendSessionInit()</tt> is called.
	 * @param from The sender's address.
	 * @param reply The reply received
	 */
	void handleSessionReply(SocketAddress from, SessionReply reply);
	
	/**
	 * Sends the {@link SessionInit} to the given {@link SocketAddress}.<br />
	 * The message is encrypted using the given {@link Cipher}. This {@code Cipher}
	 * must have been initialized.
	 * @param to The address to send to
	 * @param init The message to send
	 * @param cipher The cipher to use for encryption
	 */
	void sendSessionInit(SocketAddress to, SessionInit init, Cipher cipher);
	
	/**
	 * Handles the {@link SessionInit} from the given {@link SocketAddress} and generates a {@link SessionAck}.<br />
	 * This method generates a new {@link Nonce} and builds a new {@code SessionAck}. A {@link Cipher} is created with 
	 * the session key and <tt>sendSessionAck()</tt> is called.<br />
	 * Some informations are saved in the {@link SessionManager}.
	 * @param from The sender's address.
	 * @param init The init received
	 */
	void handleSessionInit(SocketAddress from, SessionInit init);
	
	/**
	 * Sends the {@link SessionAck} to the given {@link SocketAddress}.<br />
	 * The message is encrypted using the given {@link Cipher}. This {@code Cipher}
	 * must have been initialized.
	 * @param to The address to send to
	 * @param init The message to send
	 * @param cipher The cipher to use for encryption
	 */
	void sendSessionAck(SocketAddress to, SessionAck ack, Cipher cipher);
	
	/**
	 * Handles the {@link SessionAck} from the given {@link SocketAddress} and generates a {@link SessionOk}.<br />
	 * This method generates a new {@link Nonce} computed using the received one and builds a new {@code SessionOk}.
	 * A {@link Cipher} is created with the session key and <tt>sendSessionOk()</tt> is called.<br />
	 * The session generation is completed for this client.
	 * @param from The sender's address.
	 * @param ack The ack received
	 */
	void handleSessionAck(SocketAddress from, SessionAck ack);
	
	/**
	 * Sends the {@link SessionOk} to the given {@link SocketAddress}.<br />
	 * The message is encrypted using the given {@link Cipher}. This {@code Cipher}
	 * must have been initialized.
	 * @param to The address to send to
	 * @param init The message to send
	 * @param cipher The cipher to use for encryption
	 */
	void sendSessionOk(SocketAddress to, SessionOk ok, Cipher cipher);
	
	/**
	 * Handles the {@link SessionOk} from the given {@link SocketAddress}.<br />
	 * This method verifies the received {@link Nonce} and if valid the session generation 
	 * is completed for this client.
	 * @param from The sender's address.
	 * @param ok The ok received
	 */
	void handleSessionOk(SocketAddress from, SessionOk ok);
}
