package client.impl;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

import certification.client.ICertificationClientProtocolHandler;
import client.IClientProtocolHandler;
import protocol.Flag;
import protocol.message.AbstractMessageHandler;
import protocol.message.certification.AuthReply;
import protocol.message.certification.CertReply;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import session.client.ISessionClientProtocolHandler;
import session.client.ISessionManager;
import util.Cheat;
import util.SerializerBuffer;

public class ClientMessageHandler extends AbstractMessageHandler {

	private final Cipher rsaCipher;
	private final ISessionManager sessionManager;
	private final ISessionClientProtocolHandler sessionProtocolHandler;
	private final IClientProtocolHandler clientProtocolHandler;
	private final ICertificationClientProtocolHandler certificationProtocolHandler;
	private final SocketAddress address;
	
	public ClientMessageHandler(SerializerBuffer serializerBuffer, IClientProtocolHandler clientProtocolHandler, ICertificationClientProtocolHandler certificationProtocolHandler, ISessionClientProtocolHandler sessionProtocolHandler, ISessionManager sessionManager, SocketAddress address, Cipher rsaCipher) {
		super(serializerBuffer);
		this.sessionProtocolHandler = sessionProtocolHandler;
		this.clientProtocolHandler = clientProtocolHandler;
		this.certificationProtocolHandler = certificationProtocolHandler;
		this.sessionManager = sessionManager;
		this.address = address;
		this.rsaCipher = rsaCipher;
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			
			switch(flag) {
				case Flag.SESSION_REPLY: handleSessionReply(); break;
				case Flag.SESSION_INIT: handleSessionInit(); break;
				case Flag.SESSION_ACK: handleSessionAck(); break;
				case Flag.SESSION_OK: handleSessionOk(); break;
				case Flag.AUTH_REPLY: handleAuthReply(); break;
				case Flag.CERT_REPLY: handleCertReply(); break;
				case Flag.SERVICE_FILE_READ_REPLY: handleServiceFileRead(); break;
				case Flag.SERVICE_FILE_WRITE_REPLY: handleServiceFileWrite(); break;
				default : Cheat.LOGGER.log(Level.WARNING, "Unknown protocol flag : " + flag);
			}
		}
	}
	
	private void handleSessionReply() {
		try {
			handleEncryptedMessage(serializerBuffer, SessionReply.CREATOR, address, rsaCipher, sessionProtocolHandler::handleSessionReply);
		} catch (InvalidKeyException | ShortBufferException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while building SessionReply", e);
		}
	}
	
	private void handleSessionInit() {
		try {
			handleEncryptedMessage(serializerBuffer, SessionInit.CREATOR, address, rsaCipher, sessionProtocolHandler::handleSessionInit);
		} catch (InvalidKeyException | ShortBufferException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while building SessionInit", e);
		}
	}
	
	private void handleSessionAck() {
		try {
			int position = serializerBuffer.position();
			long id = serializerBuffer.getLong();
			serializerBuffer.position(position);
			Optional<SecretKey> secretKey = sessionManager.getSessionInfo(id).getSecretKey();
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, secretKey.get());
			handleEncryptedMessage(serializerBuffer, SessionAck.CREATOR, address, aesCipher, sessionProtocolHandler::handleSessionAck);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while building SessionAck", e);
		}
	}
	
	private void handleSessionOk() {
		try {
			int position = serializerBuffer.position();
			long id = serializerBuffer.getLong();
			serializerBuffer.position(position);
			Optional<SecretKey> secretKey = sessionManager.getSessionInfo(id).getSecretKey();
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, secretKey.get());
			handleEncryptedMessage(serializerBuffer, SessionOk.CREATOR, address, aesCipher, sessionProtocolHandler::handleSessionOk);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while building SessionOk", e);
		}
	}
	
	private void handleAuthReply() {
		handleMessage(serializerBuffer, AuthReply.CREATOR, address, certificationProtocolHandler::handleAuthReply);
	}
	
	private void handleCertReply() {
		handleMessage(serializerBuffer, CertReply.CREATOR, address, certificationProtocolHandler::handleCertReply);
	}
	
	private void handleServiceFileRead() { 
		try {
			int position = serializerBuffer.position();
			serializerBuffer.getLong();
			long sessionId = serializerBuffer.getLong();
			serializerBuffer.position(position);
			Optional<SecretKey> secretKey = sessionManager.getSessionInfo(sessionId).getSecretKey();
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, secretKey.get());
			handleEncryptedMessage(serializerBuffer, ServiceFileReadReply.CREATOR, address, aesCipher, clientProtocolHandler::handleServiceFileRead);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while building SessionOk", e);
		}
	}
	
	private void handleServiceFileWrite() { 
		handleMessage(serializerBuffer, ServiceFileWriteReply.CREATOR, address, clientProtocolHandler::handleServiceFileWrite);
	}

}
