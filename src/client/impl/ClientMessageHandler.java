package client.impl;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import certification.client.ICertificationClientProtocolHandler;
import client.IClientProtocolHandler;
import protocol.AbstractMessageHandler;
import protocol.Flag;
import protocol.message.certification.AuthReply;
import protocol.message.certification.CertReply;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.session.SessionReply;
import session.client.ISessionClientProtocolHandler;
import util.Cheat;
import util.SerializerBuffer;

public class ClientMessageHandler extends AbstractMessageHandler {

	private final Cipher rsaCipher;
	private final ISessionClientProtocolHandler sessionProtocolHandler;
	private final IClientProtocolHandler clientProtocolHandler;
	private final ICertificationClientProtocolHandler certificationProtocolHandler;
	private final SocketAddress address;
	
	public ClientMessageHandler(SerializerBuffer serializerBuffer, IClientProtocolHandler clientProtocolHandler, ICertificationClientProtocolHandler certificationProtocolHandler, ISessionClientProtocolHandler sessionProtocolHandler, SocketAddress address, Cipher rsaCipher) {
		super(serializerBuffer);
		this.sessionProtocolHandler = sessionProtocolHandler;
		this.clientProtocolHandler = clientProtocolHandler;
		this.certificationProtocolHandler = certificationProtocolHandler;
		this.address = address;
		this.rsaCipher = rsaCipher;
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			
			switch(flag) {
				case Flag.SESSION_REPLY: handleSessionReply(); break;
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
			Cheat.LOGGER.log(Level.WARNING, "Error while handling an encrypted message: ", e);
		}
	}
	
	private void handleAuthReply() {
		handleMessage(serializerBuffer, AuthReply.CREATOR, address, certificationProtocolHandler::handleAuthReply);
	}
	
	private void handleCertReply() {
		handleMessage(serializerBuffer, CertReply.CREATOR, address, certificationProtocolHandler::handleCertReply);
	}
	
	private void handleServiceFileRead() { 
		handleMessage(serializerBuffer, ServiceFileReadReply.CREATOR, address, clientProtocolHandler::handleServiceFileRead);
	}
	
	private void handleServiceFileWrite() { 
		handleMessage(serializerBuffer, ServiceFileWriteReply.CREATOR, address, clientProtocolHandler::handleServiceFileWrite);
	}

}
