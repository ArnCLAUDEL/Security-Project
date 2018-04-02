package session.client.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.ICertificationStorer;
import protocol.NetworkWriter;
import protocol.Nonce;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.ISessionManager;
import session.client.SessionIdentifier;
import session.client.SessionInfo;
import util.Cheat;
import util.KeyGenerator;

public class ConnectedSessionClientProtocolHandler extends ASessionClientProtocolHandler {
		
	protected final Map<Long, CompletableFuture<SessionIdentifier>> results;
	
	public ConnectedSessionClientProtocolHandler(NetworkWriter networkWriter, ICertificationStorer storer, ISessionManager sessionManager) {
		super(networkWriter, storer, sessionManager);
		this.results = new HashMap<>();
	}
	
	@Override
	public Future<SessionIdentifier> sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info) {
		if(request.getId() != info.getId())
			throw new IllegalArgumentException("Id of the request different from the one of the info : " + request.getId() + " != " + info.getId());
		sessionManager.createSession(info.getId(), info);
		CompletableFuture<SessionIdentifier> result = new CompletableFuture<>();
		results.put(info.getId(), result);	
		send(to, request);
		return result;
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
		try {
			SessionInfo info = sessionManager.getSessionInfo(reply.getId());
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
			sessionManager.createSession(init.getId(), new SessionInfo(init.getId(), init.getSenderAlias()));
			Nonce destinationNonce = Nonce.generate();
			SessionInfo sessionInfo = sessionManager.getSessionInfo(init.getId());
			sessionInfo.setDestinationNonce(destinationNonce);
			sessionInfo.setSecretKey(init.getSecretKey());
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
			SessionInfo info = sessionManager.getSessionInfo(ack.getId());
			info.setDestinationNonce(ack.getDestinationNonce());
			results.get(info.getId()).complete(new SessionIdentifier(ack.getId(), ack.getDestinationNonce()));
			Cipher aesCipherSender = Cipher.getInstance("AES");
			aesCipherSender.init(Cipher.ENCRYPT_MODE, info.getSecretKey().get());
			sendSessionOk(from, new SessionOk(info.getId(), Nonce.generateFrom(ack.getDestinationNonce())), aesCipherSender);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionAck.", e);
		}
	}

	@Override
	public void handleSessionOk(SocketAddress from, SessionOk ok) {
		SessionInfo info = sessionManager.getSessionInfo(ok.getId());
		if(!info.getDestinationNonce().isPresent())
			Cheat.LOGGER.log(Level.WARNING, "Session " + info.getId() + " has not set a nonce.");
		else {
			if(info.getDestinationNonce().get().validate(ok.getSenderNonce())) {
				Cheat.LOGGER.log(Level.INFO, "Session " + info.getId() + " validated");
			} else {
				sessionManager.deleteSession(info.getId());
				Cheat.LOGGER.log(Level.INFO, "Session " + info.getId() + " rejected, incorrect nonce : " + ok.getSenderNonce().getValue() + " != " + info.getDestinationNonce().get().getValue());
			}
		}
		
	}
}
