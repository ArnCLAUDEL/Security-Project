package service.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.AbstractTCPNetworkHandler;
import protocol.NetworkWriter;
import service.IServiceProvider;
import util.BiMap;
import util.SerializerBuffer;

public abstract class AServiceTCPNetworkHandler extends AbstractTCPNetworkHandler implements NetworkWriter {

	protected final ExecutorService executor;
	protected final BiMap<SocketChannel, SocketAddress> channelsAddress;
	
	public AServiceTCPNetworkHandler(ServerSocketChannel channel, IServiceProvider entity) throws IOException {
		super(channel, SelectionKey.OP_ACCEPT, entity);
		this.channelsAddress = new BiMap<>();
		this.executor = Executors.newCachedThreadPool();
	}
		
	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return write(channelsAddress.getRight(address), buffer);
	}

}
