package server.ca;

import java.net.SocketAddress;
import java.util.logging.Level;

import protocol.AbstractMessageHandler;
import protocol.message.AuthRequest;
import protocol.message.CertRequest;
import protocol.message.Flag;
import util.Cheat;
import util.SerializerBuffer;

public class CAMessageHandler extends AbstractMessageHandler {

	private final CertificationAuthority certificationAuthority;
	private final SocketAddress address;
	
	public CAMessageHandler(SerializerBuffer serializerBuffer, CertificationAuthority certificationAuthority, SocketAddress address) {
		super(serializerBuffer);
		this.certificationAuthority = certificationAuthority;
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
		handleMessage(serializerBuffer, AuthRequest.CREATOR, address, certificationAuthority::handleAuthRequest);
	}
	
	private void handleCertRequest() {
		handleMessage(serializerBuffer, CertRequest.CREATOR, address, certificationAuthority::handleCertRequest);
	}

}
