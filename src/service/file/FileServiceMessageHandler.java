package service.file;

import java.net.SocketAddress;
import java.util.logging.Level;

import certificate.CertificationProtocolHandler;
import protocol.AbstractMessageHandler;
import protocol.Flag;
import protocol.message.AuthReply;
import protocol.message.CertReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteRequest;
import util.Cheat;
import util.SerializerBuffer;

public class FileServiceMessageHandler extends AbstractMessageHandler {
		
	private final FileServiceProtocolHandler fileServiceProtocolHandler;
	private final CertificationProtocolHandler certificationProtocolHandler;
	private final SocketAddress address;
	
	public FileServiceMessageHandler(SerializerBuffer serializerBuffer, SocketAddress address, FileServiceProtocolHandler fileServiceprotocolHandler, CertificationProtocolHandler certificationProtocolHandler) {
		super(serializerBuffer);
		this.fileServiceProtocolHandler = fileServiceprotocolHandler;
		this.certificationProtocolHandler = certificationProtocolHandler;
		this.address = address;
	}

	@Override
	protected void handle() {
		synchronized (serializerBuffer) {
			byte flag = serializerBuffer.get();
			
			switch(flag) {
				case Flag.AUTH_REPLY: handleAuthReply(); break;
				case Flag.CERT_REPLY: handleCertReply(); break;
				case Flag.SERVICE_FILE_READ_REQUEST: handleServiceFileRead(); break;
				case Flag.SERVICE_FILE_WRITE_REQUEST: handleServiceFileWrite(); break;
				default : Cheat.LOGGER.log(Level.WARNING,"Unknown protocol flag : " + flag);
			}
		}
	}
	
	private void handleServiceFileRead() { 
		handleMessage(serializerBuffer, ServiceFileReadRequest.CREATOR, address, fileServiceProtocolHandler::handleServiceFileRead);
	}
	
	private void handleServiceFileWrite() { 
		handleMessage(serializerBuffer, ServiceFileWriteRequest.CREATOR, address, fileServiceProtocolHandler::handleServiceFileWrite);
	}
	
	private void handleAuthReply() {
		handleMessage(serializerBuffer, AuthReply.CREATOR, address, certificationProtocolHandler::handleAuthReply);
	}
	
	private void handleCertReply() {
		handleMessage(serializerBuffer, CertReply.CREATOR, address, certificationProtocolHandler::handleCertReply);
	}

}
