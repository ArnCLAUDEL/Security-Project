package session.client.impl;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import certification.ICertificationStorer;
import protocol.NetworkWriter;
import protocol.Nonce;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionOk;
import session.client.ISessionManager;
import session.client.SessionIdentifier;
import session.client.SessionInfo;
import util.Cheat;
import util.KeyGenerator;

public class InvalidSessionKeyConnectedSessionClientProtocolHandler extends ConnectedSessionClientProtocolHandler {

	public InvalidSessionKeyConnectedSessionClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer,
			ISessionManager sessionManager) {
		super(networkWriter, storer, sessionManager);
	}
	
	@Override
	public void handleSessionAck(SocketAddress from, SessionAck ack) {
		try {
			SessionInfo info = sessionManager.getSessionInfo(ack.getId());
			info.setDestinationNonce(ack.getDestinationNonce());
			results.get(info.getId()).complete(new SessionIdentifier(ack.getId(), ack.getDestinationNonce()));
			Cipher aesCipherSender = Cipher.getInstance("AES");
			SecretKey invalidKey = KeyGenerator.generateSecretKey();
			aesCipherSender.init(Cipher.ENCRYPT_MODE, invalidKey);
			sendSessionOk(from, new SessionOk(info.getId(), Nonce.generateFrom(ack.getDestinationNonce())), aesCipherSender);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionAck.", e);
		}
	}

}
