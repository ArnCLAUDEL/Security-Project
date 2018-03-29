package client;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.AbstractMessageHandler;
import protocol.message.AuthReply;
import protocol.message.CertReply;
import protocol.message.Flag;
import util.Cheat;
import util.SerializerBuffer;

public class ClientMessageHandler extends AbstractMessageHandler {

	private final Client client;
	private final SocketAddress address;
	
	public ClientMessageHandler(SerializerBuffer serializerBuffer, Client client, SocketAddress address) {
		super(serializerBuffer);
		this.client = client;
		this.address = address;
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			
			switch(flag) {
				case Flag.AUTH_REPLY: handleAuthReply(); break;
				case Flag.CERT_REPLY: handleCertReply(); break;
				default : Cheat.LOGGER.log(Level.WARNING, "Unknown protocol flag : " + flag);
			}
		}
	}
	
	private void handleAuthReply() {
		handleMessage(serializerBuffer, AuthReply.CREATOR, address, client::handleAuthReply);
	}
	
	private void handleCertReply() {
		handleMessage(serializerBuffer, CertReply.CREATOR, address, client::handleCertReply);
	}

}
