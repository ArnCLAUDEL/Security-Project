package service.file;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

import service.AbstractServiceUDPNetworkHandler;
import util.SerializerBuffer;

public class FileServiceUDPNetworkHandler extends AbstractServiceUDPNetworkHandler {
	private final Map<SocketAddress, FileServiceMessageHandler> handlers;
	private final FileService fileSP;
	
	public FileServiceUDPNetworkHandler(DatagramChannel channel, FileService fileSP) throws IOException {
		super(channel, fileSP);
		this.handlers = new HashMap<>();
		this.fileSP = fileSP;
	}
	
	@Override
	protected void register(SocketAddress address, SerializerBuffer buffer) {
		FileServiceMessageHandler handler = new FileServiceMessageHandler(buffer, address, fileSP, fileSP);
		handlers.put(address, handler);
		executor.execute(handler);
	}
}
