package service.file.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import service.file.IFileService;
import service.impl.AServiceTCPNetworkHandler;
import util.Cheat;
import util.SerializerBuffer;

public class FileServiceTCPNetworkHandler extends AServiceTCPNetworkHandler {

	private final Map<SocketChannel, FileServiceMessageHandler> handlers;
	private final IFileService fileSP;
	
	public FileServiceTCPNetworkHandler(ServerSocketChannel channel, IFileService fileSP) throws IOException {
		super(channel, fileSP);
		this.handlers = new HashMap<>();
		this.fileSP = fileSP;
	}
	
	@Override
	protected void register(SocketChannel channel, SerializerBuffer buffer) {
		try {
			SocketAddress address = channel.getRemoteAddress();
			channelsAddress.put(channel, address);
			FileServiceMessageHandler handler = new FileServiceMessageHandler(buffer, address, fileSP, fileSP);
			handlers.put(channel, handler);
			executor.execute(handler);
		} catch (IOException e) {
			Cheat.LOGGER.log(Level.WARNING, "Error while registering a new client.", e);
		}
	}

}
