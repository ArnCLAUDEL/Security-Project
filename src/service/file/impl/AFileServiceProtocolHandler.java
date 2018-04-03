package service.file.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import javax.crypto.Cipher;

import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.IFileService;
import service.file.IFileServiceProtocolHandler;
import service.file.IFileServiceProvider;
import session.client.ISessionManager;
import util.Cheat;

public class AFileServiceProtocolHandler extends AbstractProtocolHandler implements IFileServiceProtocolHandler {
	
	protected final IFileService service;
	protected final IFileServiceProvider provider;
	protected final ISessionManager sessionManager;
	
	public AFileServiceProtocolHandler(IFileService service, IFileServiceProvider provider, ISessionManager sessionManager, NetworkWriter networkWriter) {
		super(networkWriter);
		this.service = service;
		this.provider = provider;
		this.sessionManager = sessionManager;
	}
	
	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request, Cipher cipher) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
	}

	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteReply reply) {
		Cheat.LOGGER.log(Level.FINEST, reply + " ignored.");
	}
}
