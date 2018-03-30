package certification;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.AbstractMessageHandler;
import protocol.Flag;
import protocol.message.AuthRequest;
import protocol.message.CertRequest;
import util.Cheat;
import util.SerializerBuffer;

public class CAMessageHandler extends AbstractMessageHandler {

	private final CAProtocolHandler protocolHandler;
	private final SocketAddress address;
	
	public CAMessageHandler(SerializerBuffer serializerBuffer, CAProtocolHandler caProtocolHandler, SocketAddress address) {
		super(serializerBuffer);
		this.protocolHandler = caProtocolHandler;
		this.address = address;	
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			switch(flag) {
				case Flag.AUTH_REQUEST: handleAuthRequest(); break;
				case Flag.CERT_REQUEST: handleCertRequest(); break;
				default: Cheat.LOGGER.log(Level.WARNING, "Unknown protocol flag : " + flag);
			}
		}
	}
	
	private void handleAuthRequest() {
		handleMessage(serializerBuffer, AuthRequest.CREATOR, address, protocolHandler::handleAuthRequest);
	}
	
	private void handleCertRequest() {
		handleMessage(serializerBuffer, CertRequest.CREATOR, address, protocolHandler::handleCertRequest);
	}

}
