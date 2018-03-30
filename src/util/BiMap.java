package util;

import java.util.HashMap;
import java.util.Map;

public class BiMap<R,L> {
	private final Map<R,L> rlMap;
	private final Map<L,R> lrMap;
	
	public BiMap() {
		this.rlMap = new HashMap<>();
		this.lrMap = new HashMap<>();
	}
	
	public L getLeft(R r) {
		return rlMap.get(r);
	}
	
	public R getRight(L l) {
		return lrMap.get(l);
	}
	
	public void put(R r, L l) {
		rlMap.put(r, l);
		lrMap.put(l, r);
	}
	
	public L removeLeft(R r) {
		L l = rlMap.remove(r);
		lrMap.remove(l);
		return l;
	}
	
	public R removeRight(L l) {
		R r = lrMap.remove(l);
		rlMap.remove(r);
		return r;
	}
}
