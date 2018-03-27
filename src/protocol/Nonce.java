package protocol;

import java.util.Random;

public class Nonce implements Comparable<Nonce> {
	private final static Random RAND = new Random();
	
	private final long id;
	
	public Nonce() {
		this.id = RAND.nextLong();
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
