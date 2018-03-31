package service.file.impl;

import java.net.SocketAddress;
import java.util.logging.Level;

import certification.client.ICertificationClientProtocolHandler;
import protocol.AbstractMessageHandler;
import protocol.Flag;
import protocol.message.certification.AuthReply;
import protocol.message.certification.CertReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.IFileServiceProtocolHandler;
import util.Cheat;
import util.SerializerBuffer;

public class FileServiceMessageHandler extends AbstractMessageHandler {
		
	private final IFileServiceProtocolHandler fileServiceProtocolHandler;
	private final ICertificationClientProtocolHandler certificationProtocolHandler;
	private final SocketAddress address;
	
	public FileServiceMessageHandler(SerializerBuffer serializerBuffer, SocketAddress address, IFileServiceProtocolHandler fileServiceprotocolHandler, ICertificationClientProtocolHandler certificationProtocolHandler) {
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
