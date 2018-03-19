package protocol;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;

import io.SerializerBuffer;
import util.Cheat;

public class AbstractProtocolHandler {
	protected final static long DEFAULT_FIRST_TIME_RETRY = 10_000;
	protected final static long DEFAULT_PERIOD_RETRY = 10_000;
	
	protected final SerializerBuffer serializerBuffer;
	
	public AbstractProtocolHandler() {
		this.serializerBuffer = new SerializerBuffer();
	}
	
	protected <M extends Message> TimerTask retry(M message, Consumer<? super M> consumer) {
		return new TimerTask() {
			@Override
			public void run() {
				Cheat.LOGGER.log(Level.FINER, "Re-sending message " + message + "..");
				consumer.accept(message);
			}
		};
	}
	
	protected void schedule(TimerTask task, long firstTime, long period) {
		new Timer().scheduleAtFixedRate(task, firstTime, period);
	}
	
	protected boolean send(SerializerBuffer serializerBuffer, WritableByteChannel channel) {
		try {
			serializerBuffer.write(channel);
			return true;
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Fail to send data.", e);
			return false;
		}
	}
	
	protected Consumer<? super SerializerBuffer> getFlushCallback(WritableByteChannel channel) {
		return (serializerBuffer) -> {
			Cheat.LOGGER.log(Level.FINER, "Flushing data..");
			serializerBuffer.flip();
			send(serializerBuffer, channel);
			serializerBuffer.clear();
			Cheat.LOGGER.log(Level.FINER, "Data sent and buffer cleared.");
		};
	}
	
	protected boolean send(WritableByteChannel channel) {
		return send(serializerBuffer, channel);
	}
	
	protected synchronized boolean send(WritableByteChannel channel, Message message) {
		serializerBuffer.clear();
		serializerBuffer.put(message.getFlag());
		message.writeToBuff(serializerBuffer);
		serializerBuffer.flip();
		if(send(channel)) {
			Cheat.LOGGER.log(Level.FINER, "Message " + message + " sent.");
			return true;
		}
		return false;
	}
	
}
