package io;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import util.Cheat;

public class SerializerBuffer {
	private static final int BUFFER_SIZE = 512;
	
	private final ByteBuffer buffer;
	
	private Optional<Consumer<? super SerializerBuffer>> overflowCallback;
	private Optional<Consumer<? super SerializerBuffer>> underflowCallback;
	
	private SerializerBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
		this.overflowCallback = Optional.empty();
		this.underflowCallback = Optional.empty();
	}
	
	public SerializerBuffer(int size) {
		this(ByteBuffer.allocate(size));
	}
	
	public SerializerBuffer() {
		this(BUFFER_SIZE);
	}
	
	public void setOverflowCallback(Consumer<? super SerializerBuffer> callback) {
		this.overflowCallback = Optional.ofNullable(callback);
	}
	
	public void setUnderflowCallback(Consumer<? super SerializerBuffer> callback) {
		this.underflowCallback = Optional.ofNullable(callback);
	}
	
	private <T> T tryFlowException(Supplier<? extends T> supplier) {
		buffer.mark();
		try {
			return supplier.get();
		} catch (BufferOverflowException e) {
			Cheat.LOGGER.log(Level.FINER, "Calling overflow callback.");
			return handleFlowException(overflowCallback.orElseThrow(() -> e), supplier);
		} catch (BufferUnderflowException e) {
			Cheat.LOGGER.log(Level.FINER, "Calling underflow callback.");
			return handleFlowException(underflowCallback.orElseThrow(() -> e), supplier);
		}
	}
	
	private <T> T handleFlowException(Consumer<? super SerializerBuffer> callback, Supplier<? extends T> supplier) {
		buffer.reset();			
		callback.accept(this);
		return tryFlowException(supplier);
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}
	
	public int read(ReadableByteChannel channel) throws IOException {
		return channel.read(buffer);
	}
	
	public int write(WritableByteChannel channel) throws IOException {
		return channel.write(buffer);
	}
	
	public SerializerBuffer compact() {
		buffer.compact();
		return this;
	}
	
	public SerializerBuffer flip() {
		buffer.flip();
		return this;
	}
	
	public SerializerBuffer reset() {
		buffer.reset();
		return this;
	}
	
	public SerializerBuffer mark() {
		buffer.mark();
		return this;
	}
	
	public byte get() {
		return tryFlowException(() -> buffer.get());
	}

	public char getChar() {
		return tryFlowException(() -> buffer.getChar());
	}

	public short getShort() {
		return tryFlowException(() -> buffer.getShort());
	}

	public int getInt() {
		return tryFlowException(() -> buffer.getInt());
	}

	public long getLong() {
		return tryFlowException(() -> buffer.getLong());
	}

	public float getFloat() {
		return tryFlowException(() -> buffer.getFloat());
	}

	public double getDouble() {
		return tryFlowException(() -> buffer.getDouble());
	}

	public SerializerBuffer clear() {
		buffer.clear();
		return this;
	}
	
	public SerializerBuffer slice() {
		return new SerializerBuffer(buffer.slice());
	}
	
	public final int position() {
		return buffer.position();
	}

	public final int remaining() {
		return buffer.remaining();
	}

	public SerializerBuffer put(byte value) {
		tryFlowException(() -> buffer.put(value));
		return this;
	}

	public SerializerBuffer putChar(char value) {
		tryFlowException(() -> buffer.putChar(value));
		return this;
	}

	public SerializerBuffer putShort(short value) {
		tryFlowException(() -> buffer.putShort(value));
		return this;
	}

	public SerializerBuffer putInt(int value) {
		tryFlowException(() -> buffer.putInt(value));
		return this;
	}

	public SerializerBuffer putLong(long value) {
		tryFlowException(() -> buffer.putLong(value));
		return this;
	}

	public SerializerBuffer putFloat(float value) {
		tryFlowException(() -> buffer.putFloat(value));
		return this;
	}

	public SerializerBuffer putDouble(double value) {
		tryFlowException(() -> buffer.putDouble(value));
		return this;
	}
	
	public void putString(String s) {
		tryFlowException(() -> { 
			ByteBuffer bbs = Cheat.CHARSET.encode(s);
			int length = bbs.remaining();
			buffer.putInt(length);
			buffer.put(bbs);
			return Void.TYPE;
		});
	}
	
	public String getString() {
		return tryFlowException(() -> {
			int length = buffer.getInt();
			int limit = buffer.limit();
			if(buffer.position()+length > buffer.limit())
				throw new BufferUnderflowException();
			buffer.limit(buffer.position()+length);
			String s = Cheat.CHARSET.decode(buffer).toString();
			buffer.limit(limit);
			return s;
		});		
	}
	
	@Override
	public String toString() {
		buffer.mark();
		String res = Cheat.CHARSET.decode(buffer).toString();
		buffer.reset();
		return res;
	}
}
