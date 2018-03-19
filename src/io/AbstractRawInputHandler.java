package io;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

import util.Cheat;

public abstract class AbstractRawInputHandler extends AbstractHandler {

	protected final ReadableByteChannel channel;
	protected final SerializerBuffer serializerBuffer;
	
	private boolean stop;
	
	public AbstractRawInputHandler(ReadableByteChannel channel) {
		super();
		this.channel = channel;
		this.serializerBuffer = new SerializerBuffer();
		this.stop = false;

	}
	
	@Override
	public void shutdown() {
		stop = true;
	}
	
	@Override
	protected boolean stop() {
		return stop || !channel.isOpen();
	}
	
	@Override
	protected void handle() {
		try {
			serializerBuffer.clear();
			if(serializerBuffer.read(channel) < 0) {
				shutdown();
				return;
			}
			serializerBuffer.flip();
			handle(serializerBuffer);
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected abstract void handle(SerializerBuffer serializerBuffer) throws IOException;
	
	@Override
	public String toString() {
		return "Raw Input Handler " + Thread.currentThread().getId();
	}

}
