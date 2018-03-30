package client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import io.AbstractTCPNetworkHandler;
import protocol.NetworkWriter;
import util.BiMap;
import util.Cheat;
import util.SerializerBuffer;

public class ClientTCPNetworkHandler extends AbstractTCPNetworkHandler implements NetworkWriter {

	private final Executor executor;
	private final Map<SocketChannel, ClientMessageHandler> handlers;
	private final BiMap<SocketChannel, SocketAddress> channelsAddress;
	private final Client client;
	
	public ClientTCPNetworkHandler(ServerSocketChannel channel, Client client) throws IOException {
		super(channel, SelectionKey.OP_ACCEPT, client);
		this.executor = Executors.newCachedThreadPool();
		this.handlers = new HashMap<>();
		this.channelsAddress = new BiMap<>();
		this.client = client;
	}

	@Override
	protected void register(SocketChannel channel, SerializerBuffer buffer) {
		try {
			SocketAddress address = channel.getRemoteAddress();
			channelsAddress.put(channel, address);
			ClientMessageHandler handler = new ClientMessageHandler(buffer, client, client, address);
			handlers.put(channel, handler);
			executor.execute(handler);
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while registering a new client.", e);
		}
	}

	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return write(channelsAddress.getRight(address), buffer);
	}
}
