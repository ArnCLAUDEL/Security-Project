package protocol.message;

import java.nio.BufferUnderflowException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import io.AbstractHandler;
import util.Cheat;
import util.Creator;
import util.SerializerBuffer;
import util.TriConsumer;

/**
 * {@code AbstractMessageHandler} provides a base implementation for a
 * message handler.<br />
 * This works with an existing {@link SerializerBuffer} and it will 
 * simply add to it an {@link BufferUnderflowException} callback.
 * Hence when it will try to read, from the buffer, some data receive from the network,
 * and a {@code BufferUnderflowException} occurs, it will wait on the buffer.
 * @see AbstractHandler
 * @see SerializerBuffer
 * @see BufferUnderflowException
 */
public abstract class AbstractMessageHandler extends AbstractHandler {
	
	/**
	 * The buffer to read from.
	 */
	protected final SerializerBuffer serializerBuffer;
	
	/**
	 * Indicates if the handler should stop or not.
	 */
	private boolean stop;

	/**
	 * Creates a new instance with the given buffer.<br />
	 * The buffer's methods <tt>clear()</tt> and <tt>flip()</tt> are called.
	 * An {@link BufferUnderflowException} callback is also set.
	 * @param serializerBuffer The buffer to read the data from.
	 */
	public AbstractMessageHandler(SerializerBuffer serializerBuffer) {
		super();
		this.serializerBuffer = serializerBuffer;
		this.serializerBuffer.clear();
		this.serializerBuffer.flip();
		this.serializerBuffer.setUnderflowCallback(underflowCallback());
		this.stop = false;
	}
	
	/**
	 * Returns an {@link BufferUnderflowException} callback that will wait
	 * on the buffer and stops this handler if interrupted.
	 * @return The {@code BufferUnderflowException} callback.
	 */
	protected Consumer<? super SerializerBuffer> underflowCallback() {
		return (serializerBuffer) -> {
			try {
				synchronized (serializerBuffer) {
					Cheat.LOGGER.log(Level.FINEST, "Waiting for re-filling..");
					serializerBuffer.wait();
				}
			} catch (InterruptedException e) {
				shutdown();
			}
		};
	}
	
	protected <M extends Message, T> void handleMessage(SerializerBuffer serializerBuffer, Creator<M> messageCreator, T info, BiConsumer<T, M> handler) {
		M message = messageCreator.init();
		message.readFromBuff(serializerBuffer);
		Cheat.LOGGER.log(Level.FINER, message + " received.");
		handler.accept(info, message);
	}
	
	protected <M extends EncryptedMessage, T> void handleEncryptedMessage(SerializerBuffer serializerBuffer, Creator<M> messageCreator, T info, Cipher cipher, BiConsumer<T, M> handler) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		M message = messageCreator.init();
		message.readFromBuff(serializerBuffer, cipher);
		Cheat.LOGGER.log(Level.FINER, message + " received.");
		handler.accept(info, message);
	}	
	
	protected <M extends EncryptedMessage, T, U extends Cipher> void handleEncryptedMessage(SerializerBuffer serializerBuffer, Creator<M> messageCreator, T info, U cipher, TriConsumer<T, M, U> handler) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		M message = messageCreator.init();
		message.readFromBuff(serializerBuffer, cipher);
		Cheat.LOGGER.log(Level.FINER, message + " received.");
		handler.accept(info, message, cipher);
	}
	
	/**
	 * Stops the handler and notify everyone waiting on the buffer.
	 */
	@Override
	public void shutdown() {
		stop = true;
		synchronized (serializerBuffer) {
			serializerBuffer.notifyAll();
		}
	}
	
	@Override
	protected boolean stop() {
		return stop;
	}	
	
}
