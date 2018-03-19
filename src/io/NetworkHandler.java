package io;

import java.nio.channels.SelectionKey;

public interface NetworkHandler {
	public void handleAcceptOperation(SelectionKey sk);
	public void handleReadOperation(SelectionKey sk, SerializerBuffer serializerBuffer);
	public void handleWriteOperation(SelectionKey sk, SerializerBuffer serializerBuffer);
	public void handleConnectOperation(SelectionKey sk);
}
