package io;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import util.Cheat;

public abstract class AbstractNetworkHandler extends AbstractHandler {
	private final Map<SelectableChannel, Integer> channelOps = new HashMap<>();
	private final Map<SelectableChannel, SerializerBuffer> channelBuffers = new HashMap<>();
	private final Selector selector;
	private final IOEntity ioEntity;
	
	private boolean stop;
	
	public AbstractNetworkHandler(SelectableChannel channel, int op, IOEntity ioEntity) throws IOException {
		super();
		this.selector = Selector.open();
		this.ioEntity = ioEntity;
		addChannel(channel, op);
		this.stop = false;
	}
	
	protected void addChannel(SelectableChannel channel, int op) throws IOException {
		channel.configureBlocking(false);
		channel.register(selector, op);
		channelOps.put(channel, op);
		channelBuffers.put(channel, new SerializerBuffer());
	}
	
	protected SerializerBuffer getSerializerBuffer(SelectableChannel channel) {
		return channelBuffers.get(channel);
	}
	
	@Override
	protected boolean stop() {
		return !ioEntity.isActive() || stop;
	}
	
	@Override
	public void shutdown() {
		stop = true;
	}
	
	private boolean checkOps(SelectionKey sk, int op) {
		return (sk.readyOps() & op & channelOps.get(sk.channel())) != 0;
	}
	
	@Override
	public void handle() {
		try {
			Iterator<SelectionKey> itr;
			SelectionKey sk;
			selector.select();
			itr = selector.selectedKeys().iterator();
			while(itr.hasNext()) {
				sk = itr.next();
				if(checkOps(sk, SelectionKey.OP_ACCEPT))
					handleAcceptOperation(sk);
				
				else if(checkOps(sk, SelectionKey.OP_READ))
					handleReadOperation(sk, channelBuffers.get(sk.channel()));
				
				itr.remove();
			}
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected void channelClosedCallback(SelectionKey sk) throws IOException {
		sk.cancel();
		sk.channel().close();
	}
	
	protected void handleAcceptOperation(SelectionKey sk) {
		Cheat.LOGGER.log(Level.FINER, "Handling ACCEPT_OPERATION..");
	}

	protected void handleReadOperation(SelectionKey sk, SerializerBuffer serializerBuffer) {
		Cheat.LOGGER.log(Level.FINER, "Handling READ_OPERATION..");
		SocketChannel sc = (SocketChannel) sk.channel();
		try {
			synchronized (serializerBuffer) {
				serializerBuffer.compact();
				int read = serializerBuffer.read(sc);
				if(read < 0) {
					channelClosedCallback(sk);
				}
				serializerBuffer.flip();
				serializerBuffer.notifyAll();
			}
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while handling READ_OPERATION.", e);
		}
	}

	@Override
	public String toString() {
		return "Network Handler " + Thread.currentThread().getId();
	}
}
