package client;

import java.net.SocketAddress;
import java.util.logging.Level;

import certification.CertificationProtocolHandler;
import protocol.AbstractMessageHandler;
import protocol.Flag;
import protocol.message.AuthReply;
import protocol.message.CertReply;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileWriteReply;
import util.Cheat;
import util.SerializerBuffer;

public class ClientMessageHandler extends AbstractMessageHandler {

	private final ClientProtocolHandler clientProtocolHandler;
	private final CertificationProtocolHandler certificationProtocolHandler;
	private final SocketAddress address;
	
	public ClientMessageHandler(SerializerBuffer serializerBuffer, ClientProtocolHandler clientProtocolHandler, CertificationProtocolHandler certificationProtocolHandler, SocketAddress address) {
		super(serializerBuffer);
		this.clientProtocolHandler = clientProtocolHandler;
		this.certificationProtocolHandler = certificationProtocolHandler;
		this.address = address;
	}

	@Override
	protected void handle() {
		synchronized(serializerBuffer) {
			byte flag = serializerBuffer.get();
			
			switch(flag) {
				case Flag.AUTH_REPLY: handleAuthReply(); break;
				case Flag.CERT_REPLY: handleCertReply(); break;
				case Flag.SERVICE_FILE_READ_REPLY: handleServiceFileRead(); break;
				case Flag.SERVICE_FILE_WRITE_REPLY: handleServiceFileWrite(); break;
				default : Cheat.LOGGER.log(Level.WARNING, "Unknown protocol flag : " + flag);
			}
		}
	}
	
	private void handleAuthReply() {
		handleMessage(serializerBuffer, AuthReply.CREATOR, address, certificationProtocolHandler::handleAuthReply);
	}
	
	private void handleCertReply() {
		handleMessage(serializerBuffer, CertReply.CREATOR, address, certificationProtocolHandler::handleCertReply);
	}
	
	private void handleServiceFileRead() { 
		handleMessage(serializerBuffer, ServiceFileReadReply.CREATOR, address, clientProtocolHandler::handleServiceFileRead);
	}
	
	private void handleServiceFileWrite() { 
		handleMessage(serializerBuffer, ServiceFileWriteReply.CREATOR, address, clientProtocolHandler::handleServiceFileWrite);
	}

}
