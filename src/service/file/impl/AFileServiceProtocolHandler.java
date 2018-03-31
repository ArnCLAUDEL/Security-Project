package service.file.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import certification.ICertificationStorer;
import certification.client.impl.ConnectedCertificationClientProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.IFileService;
import service.file.IFileServiceProtocolHandler;
import service.file.IFileServiceProvider;
import util.Cheat;

public class AFileServiceProtocolHandler extends ConnectedCertificationClientProtocolHandler implements IFileServiceProtocolHandler {
	
	protected final IFileService service;
	protected final IFileServiceProvider provider;
	
	public AFileServiceProtocolHandler(IFileService service, ICertificationStorer storer, IFileServiceProvider provider, NetworkWriter networkWriter) {
		super(networkWriter, storer);
		this.service = service;
		this.provider = provider;
	}
	
	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request) {
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
