package service.file.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

import service.file.IFileService;
import service.impl.AServiceUDPNetworkHandler;
import util.SerializerBuffer;

public class FileServiceUDPNetworkHandler extends AServiceUDPNetworkHandler {
	private final Map<SocketAddress, FileServiceMessageHandler> handlers;
	private final IFileService fileSP;
	
	public FileServiceUDPNetworkHandler(DatagramChannel channel, IFileService fileSP) throws IOException {
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
