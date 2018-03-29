package protocol;

import java.io.IOException;
import java.net.SocketAddress;

import util.SerializerBuffer;

public interface NetworkWriter {
	int write(SocketAddress address, SerializerBuffer buffer) throws IOException;
}
