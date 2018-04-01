package client;

import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import javax.crypto.NoSuchPaddingException;

import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import session.client.SessionInfo;

public interface IClientProtocolHandler {
	CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request, SessionInfo info) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException;
	void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request);
	void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply);
	void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply);
}
