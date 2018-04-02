package certification.server.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import javax.crypto.Cipher;

import certification.server.ICertificationServerProtocolHandler;
import protocol.Flag;
import protocol.message.AbstractMessageHandler;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertRequest;
import protocol.message.session.SessionRequest;
import session.server.ISessionServerProtocolHandler;
import util.Cheat;
import util.SerializerBuffer;

public class CertificationServerMessageHandler extends AbstractMessageHandler {

	private final Cipher rsaCipher;
	private final ICertificationServerProtocolHandler protocolHandler;
	private final ISessionServerProtocolHandler sessionProtocolHandler;
	private final SocketAddress address;
	
	public CertificationServerMessageHandler(SerializerBuffer serializerBuffer, ICertificationServerProtocolHandler caProtocolHandler, ISessionServerProtocolHandler sessionProtocolHandler, SocketAddress address, Cipher rsaCipher)	 {
		super(serializerBuffer);
		this.protocolHandler = caProtocolHandler;
		this.sessionProtocolHandler = sessionProtocolHandler;
		this.address = address;	
		this.rsaCipher = rsaCipher; 
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			switch(flag) {
				case Flag.AUTH_REQUEST: handleAuthRequest(); break;
				case Flag.CERT_REQUEST: handleCertRequest(); break;
				case Flag.SESSION_REQUEST: handleSessionRequest(); break;
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
	
	private void handleSessionRequest() {
		handleMessage(serializerBuffer, SessionRequest.CREATOR, address, sessionProtocolHandler::handleSessionRequest);
	}

}
