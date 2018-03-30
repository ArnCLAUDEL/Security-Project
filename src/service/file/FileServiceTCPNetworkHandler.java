package service.file;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import service.AbstractServiceTCPNetworkHandler;
import util.Cheat;
import util.SerializerBuffer;

public class FileServiceTCPNetworkHandler extends AbstractServiceTCPNetworkHandler {

	private final Map<SocketChannel, FileServiceMessageHandler> handlers;
	private final FileService fileSP;
	
	public FileServiceTCPNetworkHandler(ServerSocketChannel channel, FileService fileSP) throws IOException {
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
