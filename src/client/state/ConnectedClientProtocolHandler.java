package client.state;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import util.Cheat;

public class ConnectedClientProtocolHandler extends AbstractClientProtocolHandler {

	public ConnectedClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}
	
	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request) {
		send(to, request);
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
