package client.impl;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import client.IClientProtocolHandler;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import util.Cheat;

public abstract class AClientProtocolHandler extends AbstractProtocolHandler implements IClientProtocolHandler {

	public AClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}

	@Override
	public CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	
	
	
	
}
