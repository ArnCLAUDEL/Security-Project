package protocol;

import java.util.Random;

import io.MySerializable;
import util.Creator;
import util.SerializerBuffer;

public class Nonce implements Comparable<Nonce>, MySerializable {
	public final static Creator<Nonce> CREATOR = Nonce::new;
	
	private final static Random RANDOM = new Random();
	
	private long id;
	
	private Nonce() {
		
	}
	
	private Nonce(long id) {
		this.id = id;
	}
	
	public static Nonce generate() {
		return new Nonce(RANDOM.nextLong());
	}
	
	public long getValue() {
		return id;
	}
	
	@Override
	public void writeToBuff(SerializerBuffer ms) {
		ms.putLong(id);
	}

	@Override
	public void readFromBuff(SerializerBuffer ms) {
		this.id = ms.getLong();
	}
	
	@Override
	public int compareTo(Nonce n) {
		return (int) (id-n.id);
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}
	
}
