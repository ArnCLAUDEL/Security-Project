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
	
	public static Nonce generateFrom(Nonce nonce) {
		return new Nonce(nonce.getValue() -1);
	}
	
	public boolean validate(Nonce nonce) {
		return (id-1) == nonce.id;
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
	public boolean equals(Object obj) {
		if(!(obj instanceof Nonce))
			return false;
		return id == ((Nonce) obj).id;
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
