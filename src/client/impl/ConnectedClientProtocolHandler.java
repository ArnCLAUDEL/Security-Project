package client.impl;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import session.client.SessionInfo;
import util.Cheat;

public class ConnectedClientProtocolHandler extends AClientProtocolHandler {

	private final Map<Long, CompletableFuture<String>> results;
	
	public ConnectedClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
		this.results = new TreeMap<>();
	}
	
	@Override
	public CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request, SessionInfo info) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		CompletableFuture<String> result = new CompletableFuture<>();
		results.put(request.getId(), result);
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, info.getSecretKey().get());
		send(to, request, aesCipher);
		return result;
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request) {
		send(to, request);
	}

	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply) {
		StringBuilder builder = new StringBuilder();
		if(reply.getErrorMessage().isPresent()) {
			builder.append("Error while reading file " + reply.getFilename() + "\n");
			builder.append(reply.getErrorMessage());
		} else {
			builder.append("Data received from file " + reply.getFilename() + "\n");
			builder.append(reply.getContent());
		}
		results.get(reply.getId()).complete(reply.getContent());
		Cheat.LOGGER.log(Level.INFO, builder.toString());
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply) {
		StringBuilder builder = new StringBuilder();
		if(reply.getErrorMessage().isPresent()) {
			builder.append("Error while writing to file " + reply.getFilename() + "\n");
			builder.append(reply.getErrorMessage());
		} else {
			builder.append("Data written to file " + reply.getFilename() + "\n");
		}
		Cheat.LOGGER.log(Level.INFO, builder.toString());
	}

}
