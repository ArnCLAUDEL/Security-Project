package service.file;

import java.net.SocketAddress;

import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;

public interface FileServiceProtocolHandler {
	void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request);
	void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request);
	void sendServiceFileRead(SocketAddress to, ServiceFileReadReply reply);
	void sendServiceFileWrite(SocketAddress to, ServiceFileWriteReply reply);
}
