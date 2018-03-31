package session.client.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.impl.CertificationStorer;
import protocol.NetworkWriter;
import protocol.Nonce;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.SessionInfo;
import util.Cheat;
import util.KeyGenerator;

public class ConnectedSessionClientProtocolHandler extends ASessionClientProtocolHandler {

	private final Map<Long, SessionInfo> sessionsInfo;
	private final Map<Long, Nonce> idNonces;
	
	
	public ConnectedSessionClientProtocolHandler(NetworkWriter networkWriter, CertificationStorer storer) {
		super(networkWriter, storer);
		this.sessionsInfo = new HashMap<>();
		this.idNonces = new HashMap<>();
	}
	
	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info) {
		if(request.getId() != info.getId())
			throw new IllegalArgumentException("Id of the request different from the one of the infos : " + request.getId() + " != " + info.getId());
		sessionsInfo.put(request.getId(), info);
		send(to, request);
	}
	
	@Override
	public void sendSessionInit(SocketAddress to, SessionInit init, Cipher cipher) {
		send(to, init, cipher);
	}
	
	@Override
	public void sendSessionAck(SocketAddress to, SessionAck ack, Cipher cipher) {
		send(to, ack, cipher);
	}
	
	@Override
	public void sendSessionOk(SocketAddress to, SessionOk ok, Cipher cipher) {
		send(to, ok, cipher);
	}
	
	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {		
		StringBuilder sb = new StringBuilder();
		sb.append("Session reply :"
				+ "id : " + reply.getId() + "\n"
				+ "destination : " + reply.getDestinationAlias() + "\n"
				+ "secretKey : " + reply.getSecretKey());
		
		Cheat.LOGGER.log(Level.INFO, sb.toString());
		
		
		
		try {
			SessionInfo info = sessionsInfo.get(reply.getId());
			info.setSecretKey(reply.getSecretKey());
			Cipher rsaCipherDestination = Cipher.getInstance("RSA");
			BCRSAPublicKey publicKeyDestination = KeyGenerator.bcrsaPublicKeyConverter(info.getCertificateHolder());
			rsaCipherDestination.init(Cipher.PUBLIC_KEY, publicKeyDestination);
			
			sendSessionInit(info.getDestinationAddress(), new SessionInit(reply.getId(), reply.getSecretKey(), info.getSenderAlias()), rsaCipherDestination);
		} catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | IOException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionRely.", e);
		}
	}

	@Override
	public void handleSessionInit(SocketAddress from, SessionInit init) {
		try {
			Nonce destinationNonce = Nonce.generate();
			idNonces.put(init.getId(), destinationNonce);
			Cipher aesCipherSender = Cipher.getInstance("AES");
			aesCipherSender.init(Cipher.ENCRYPT_MODE, init.getSecretKey());
			sendSessionAck(from, new SessionAck(init.getId(), destinationNonce), aesCipherSender);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionInit.", e);
		}
	}

	@Override
	public void handleSessionAck(SocketAddress from, SessionAck ack) {
		try {
			SessionInfo info = sessionsInfo.get(ack.getId());
			Cipher aesCipherSender = Cipher.getInstance("AES");
			aesCipherSender.init(Cipher.ENCRYPT_MODE, info.getSecretKey().get());
			sendSessionOk(from, new SessionOk(info.getId(), Nonce.generateFrom(ack.getDestinationNonce())), aesCipherSender);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionInit.", e);
		}
	}

	@Override
	public void handleSessionOk(SocketAddress from, SessionOk ok) {
		SessionInfo info = sessionsInfo.get(ok.getId());
		if(idNonces.get(ok.getId()).validate(ok.getSenderNonce())) {
			Cheat.LOGGER.log(Level.INFO, "Session " + info.getId() + " validated");
		} else {
			Cheat.LOGGER.log(Level.INFO, "Session " + info.getId() + " rejected, incorrect nonce.");
		}
		
	}
	
	

}
