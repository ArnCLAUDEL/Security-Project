package service.file.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import certification.ICertificationStorer;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.IFileService;
import service.file.IFileServiceProvider;
import session.client.ISessionManager;
import session.client.SessionInfo;
import util.Cheat;

public class ConnectedFileServiceProtocolHandler extends AFileServiceProtocolHandler {
	
	public ConnectedFileServiceProtocolHandler(IFileService service, ICertificationStorer storer, IFileServiceProvider provider, ISessionManager sessionManager, NetworkWriter networkWriter) {
		super(service, storer, provider, sessionManager, networkWriter);
	}
	
	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request, Cipher cipher) {
		String filename = request.getFilename();
		ServiceFileReadReply reply;
		if(!sessionManager.checkSessionIdentifier(request.getSessionIdentifier())) {
			reply =  new ServiceFileReadReply(request.getId(), filename, "", "Incorrect Session Identifier", request.getSessionIdentifier());
		} else {
			try {
				String content = provider.read(filename);
				reply = new ServiceFileReadReply(filename, content, request.getId(), request.getSessionIdentifier());;
			} catch (IOException e) {
				Cheat.LOGGER.log(Level.WARNING, "Error while reading from file : " + filename, e);
				reply = new ServiceFileReadReply(request.getId(), filename, "", e.getMessage(), request.getSessionIdentifier());
			}
		}
		sendServiceFileRead(from, reply);
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request) {
		String filename = request.getFilename();
		if(!sessionManager.checkSessionIdentifier(request.getSessionIdentifier())) {
			sendServiceFileWrite(from, new ServiceFileWriteReply(request.getId(), filename, "Incorrect Session Identifier", request.getSessionIdentifier()));
		}
		
		try {
			provider.write(filename, request.getContent());
			sendServiceFileWrite(from, new ServiceFileWriteReply(request.getId(), filename, request.getSessionIdentifier()));
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while writing to file : " + filename, e);
			sendServiceFileWrite(from, new ServiceFileWriteReply(request.getId(), filename, e.getMessage(), request.getSessionIdentifier()));
		}
	}

	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadReply reply) {
		try {
			Cipher aesCipher = Cipher.getInstance("AES");
			SessionInfo info = sessionManager.getSessionInfo(reply.getSessionIdentifier().getId());
			aesCipher.init(Cipher.ENCRYPT_MODE, info.getSecretKey().get());
			send(to, reply, aesCipher);	
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while writing to file : " + reply.getFilename(), e);
		}		
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteReply reply) {
		send(to, reply);
	}

}
