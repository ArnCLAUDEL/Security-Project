package service.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.AbstractUDPNetworkHandler;
import protocol.NetworkWriter;
import service.IServiceProvider;
import util.SerializerBuffer;

public abstract class AServiceUDPNetworkHandler extends AbstractUDPNetworkHandler implements NetworkWriter {
	protected final ExecutorService executor;
	
	public AServiceUDPNetworkHandler(DatagramChannel channel, IServiceProvider entity) throws IOException {
		super(channel, SelectionKey.OP_READ, entity);
		this.executor = Executors.newCachedThreadPool();
	}
		
	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return send(address, buffer);
	}
}
