package client.state;

import java.net.SocketAddress;
import java.util.logging.Level;

import client.ClientProtocolHandler;
import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import util.Cheat;

public class AbstractClientProtocolHandler extends AbstractProtocolHandler implements ClientProtocolHandler {

	public AbstractClientProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}

	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request) {
		Cheat.LOGGER.log(Level.FINEST, request + " ignored.");
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
