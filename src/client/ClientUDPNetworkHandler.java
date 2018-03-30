package client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.AbstractUDPNetworkHandler;
import protocol.NetworkWriter;
import util.SerializerBuffer;

public class ClientUDPNetworkHandler extends AbstractUDPNetworkHandler implements NetworkWriter {
	
	private final ExecutorService executor;
	private final Map<SocketAddress, ClientMessageHandler> messageHandlers;
	private final Client client;
	
	public ClientUDPNetworkHandler(DatagramChannel channel, Client client) throws IOException {
		super(channel, SelectionKey.OP_READ, client);
		this.client = client;
		this.executor = Executors.newCachedThreadPool();
		this.messageHandlers = new HashMap<>();
	}
	
	@Override
	protected void register(SocketAddress address, SerializerBuffer serializerBuffer) {
		ClientMessageHandler handler = new ClientMessageHandler(serializerBuffer, client, client, address);
		executor.execute(handler);
		messageHandlers.put(address, handler);
	}

	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return send(address, buffer);
	}
	
}
