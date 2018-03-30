package service.file.state;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Level;

import certificate.CertificationStorer;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.FileService;
import service.file.FileServiceProvider;
import util.Cheat;

public class ConnectedFileServiceProtocolHandler extends AbstractFileServiceProtocolHandler {
	
	public ConnectedFileServiceProtocolHandler(FileService service, CertificationStorer storer, FileServiceProvider provider, NetworkWriter networkWriter) {
		super(service, storer, provider, networkWriter);
	}
	
	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request) {
		String filename = request.getFilename();
		try {
			String content = provider.read(filename);
			sendServiceFileRead(from, new ServiceFileReadReply(filename, content));
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while reading from file : " + filename, e);
			sendServiceFileRead(from, new ServiceFileReadReply(filename, "", e.getMessage()));
		}
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request) {
		String filename = request.getFilename();
		try {
			provider.write(filename, request.getContent());
			sendServiceFileWrite(from, new ServiceFileWriteReply(filename));
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while writing to file : " + filename, e);
			sendServiceFileWrite(from, new ServiceFileWriteReply(filename, e.getMessage()));
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
