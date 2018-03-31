package client;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;

public interface IClientProtocolHandler {
	CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request);
	void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request);
	void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply);
	void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply);
}
