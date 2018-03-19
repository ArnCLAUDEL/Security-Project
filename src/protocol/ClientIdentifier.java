package protocol;

import java.nio.channels.WritableByteChannel;

import util.Cheat;

public class ClientIdentifier implements Comparable<ClientIdentifier> {
	
	private final long id;
	private final String name;
	
	private WritableByteChannel channel;
	
	public ClientIdentifier(String name, WritableByteChannel channel) {
		if(channel == null)
			throw new IllegalArgumentException("Excepting a non-null WritableByteChannel");
		
		this.id = channel.hashCode() + Cheat.RANDOM.nextInt(1_000_000);
		this.name = name;
		this.channel = channel;
	}

	@Override
	public int compareTo(ClientIdentifier clientId) {
		return (int) (this.id - clientId.id);
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public WritableByteChannel getChannel() {
		return channel;
	}
	
	@Override
	public String toString() {
		return "Client " + id;
	}
}
