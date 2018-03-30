package client;

import java.net.SocketAddress;

import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;

public interface ClientProtocolHandler {
	void sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request);
	void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request);
	void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply);
	void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply);
}
