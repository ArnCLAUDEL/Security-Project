package service.file.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Level;

import certification.ICertificationStorer;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.IFileService;
import service.file.IFileServiceProvider;
import util.Cheat;

public class ConnectedFileServiceProtocolHandler extends AFileServiceProtocolHandler {
	
	public ConnectedFileServiceProtocolHandler(IFileService service, ICertificationStorer storer, IFileServiceProvider provider, NetworkWriter networkWriter) {
		super(service, storer, provider, networkWriter);
	}
	
	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request) {
		String filename = request.getFilename();
		try {
			String content = provider.read(filename);
			sendServiceFileRead(from, new ServiceFileReadReply(filename, content, request.getId()));
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while reading from file : " + filename, e);
			sendServiceFileRead(from, new ServiceFileReadReply(request.getId(), filename, "", e.getMessage()));
		}
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request) {
		String filename = request.getFilename();
		try {
			provider.write(filename, request.getContent());
			sendServiceFileWrite(from, new ServiceFileWriteReply(request.getId(), filename));
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while writing to file : " + filename, e);
			sendServiceFileWrite(from, new ServiceFileWriteReply(request.getId(), filename, e.getMessage()));
		}
	}

	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadReply reply) {
		send(to, reply);
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteReply reply) {
		send(to, reply);
	}

}
