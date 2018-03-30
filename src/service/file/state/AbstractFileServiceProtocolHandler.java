package service.file.state;

import java.net.SocketAddress;
import java.util.logging.Level;

import certificate.CertificationStorer;
import certificate.ConnectedCertificationProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.FileService;
import service.file.FileServiceProtocolHandler;
import service.file.FileServiceProvider;
import util.Cheat;

public class AbstractFileServiceProtocolHandler extends ConnectedCertificationProtocolHandler implements FileServiceProtocolHandler {
	
	protected final FileService service;
	protected final FileServiceProvider provider;
	
	public AbstractFileServiceProtocolHandler(FileService service, CertificationStorer storer, FileServiceProvider provider, NetworkWriter networkWriter) {
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
