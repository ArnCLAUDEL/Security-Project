package certification.server.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.AbstractUDPNetworkHandler;
import protocol.NetworkWriter;
import util.SerializerBuffer;

public class CertificationServerNetworkHandler extends AbstractUDPNetworkHandler implements NetworkWriter {
	private final static long DEFAULT_SHUTDOWN_DELAY = 36_000;
	
	private final ExecutorService executor;
	private final Map<SocketAddress, CertificationServerMessageHandler> messageHandlers;
	private final CertificationServer certificationAuthority;
	
	public CertificationServerNetworkHandler(DatagramChannel channel, CertificationServer certificationAuthority) throws IOException {
		super(channel, SelectionKey.OP_READ, certificationAuthority);
		this.certificationAuthority = certificationAuthority;
		this.executor = Executors.newCachedThreadPool();
		this.messageHandlers = new HashMap<>();
	}
	
	@Override
	protected void register(SocketAddress address, SerializerBuffer serializerBuffer) {
		CertificationServerMessageHandler handler = new CertificationServerMessageHandler(serializerBuffer, certificationAuthority, certificationAuthority,address);
		executor.execute(handler);
		// TODO scheduleAutoShutdown(handler);
		messageHandlers.put(address, handler);
	}
	
	private void scheduleAutoShutdown(CertificationServerMessageHandler handler) {
		scheduleAutoShutdown(handler, DEFAULT_SHUTDOWN_DELAY);
	}
	
	private void scheduleAutoShutdown(CertificationServerMessageHandler handler, long delay) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				handler.shutdown();
			}
		};
		new Timer().schedule(task, delay);
	}

	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return send(address, buffer);
	}

}
