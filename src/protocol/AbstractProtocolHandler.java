package protocol;

import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;

import util.Cheat;
import util.SerializerBuffer;

/**
 * {@code AbstractProtocolHandler} is a base implementation of any
 * protocol handler.<br />
 * This provides a dedicated {@link SerializerBuffer} and a mechanism
 * to retry a given task. This could be used when sending {@link Message}
 * to the network.
 */
public class AbstractProtocolHandler {
	
	/**
	 * The default delay to retry the first time.
	 */
	protected final static long DEFAULT_FIRST_TIME_RETRY = 10_000;
	
	/**
	 * The default period of the retry.
	 */
	protected final static long DEFAULT_PERIOD_RETRY = 10_000;
	
	//TODO delays in instance fields
	
	/**
	 * The dedicated buffer.
	 */
	protected final SerializerBuffer serializerBuffer;
	
	/**
	 * Creates a new instance with a new dedicated {@link SerializerBuffer}.
	 */
	public AbstractProtocolHandler() {
		this.serializerBuffer = new SerializerBuffer();
	}
	
	/**
	 * Creates and returns a {@link TimerTask} wich will call the given task.<br />
	 * Usually, the task will simply retry to send the {@link Message}.
	 * @param message The message to consume.
	 * @param consumer The task that will consume the message.
	 * @return
	 */
	protected <M extends Message> TimerTask retry(M message, Consumer<? super M> consumer) {
		return new TimerTask() {
			@Override
			public void run() {
				Cheat.LOGGER.log(Level.FINER, "Re-sending message " + message + "..");
				consumer.accept(message);
			}
		};
	}
	
	/**
	 * Schedules the given {@link TimerTask} using the given delay and period.<br />
	 * This will create a {@link Timer} and call the method 
	 * <tt>scheduleAtFixedRate(task,firstTime,period)</tt>.<br />
	 * <b>Note</b> that the task is never stopped and should be stopped by the caller.
	 * @param task The task to schedule
	 * @param firstTime The delay to use
	 * @param period The period to use
	 */
	protected void schedule(TimerTask task, long firstTime, long period) {
		//new Timer().scheduleAtFixedRate(task, firstTime, period);
	}
	
}
