package session.client.impl;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import protocol.NetworkWriter;
import protocol.Nonce;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionOk;
import session.client.ISessionManager;
import session.client.SessionIdentifier;
import session.client.SessionInfo;
import util.Cheat;

public class IncorrectNonceConnectedSessionClientProcolHandler extends ConnectedSessionClientProtocolHandler {

	public IncorrectNonceConnectedSessionClientProcolHandler(NetworkWriter networkWriter, ISessionManager sessionManager) {
		super(networkWriter, sessionManager);
	}
	
	@Override
	public void handleSessionAck(SocketAddress from, SessionAck ack) {
		try {
			SessionInfo info = sessionManager.getSessionInfo(ack.getId());
			info.setDestinationNonce(ack.getDestinationNonce());
			results.get(info.getId()).complete(new SessionIdentifier(ack.getId(), ack.getDestinationNonce()));
			Cipher aesCipherSender = Cipher.getInstance("AES");
			aesCipherSender.init(Cipher.ENCRYPT_MODE, info.getSecretKey().get());
			sendSessionOk(from, new SessionOk(info.getId(), Nonce.generate()), aesCipherSender);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionAck.", e);
		}
	}

}
