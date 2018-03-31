package service.file.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import service.impl.AServiceUDPNetworkHandler;
import util.SerializerBuffer;

public class FileServiceUDPNetworkHandler extends AServiceUDPNetworkHandler {
	private final Map<SocketAddress, FileServiceMessageHandler> handlers;
	private final FileService fileSP;
	private final Cipher rsaCipher;
	
	public FileServiceUDPNetworkHandler(DatagramChannel channel, FileService fileSP, Cipher rsaCipher) throws IOException {
		super(channel, fileSP);
		this.handlers = new HashMap<>();
		this.fileSP = fileSP;
		this.rsaCipher = rsaCipher;
	}
	
	@Override
	protected void register(SocketAddress address, SerializerBuffer buffer) {
		FileServiceMessageHandler handler = new FileServiceMessageHandler(buffer, address, fileSP, fileSP, fileSP, fileSP, rsaCipher);
		handlers.put(address, handler);
		executor.execute(handler);
	}
}
