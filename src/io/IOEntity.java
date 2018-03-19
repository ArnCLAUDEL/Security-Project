package io;

public interface IOEntity extends Runnable {
	boolean isActive();
	void shutdown();
	void addHandler(AbstractHandler handler);
	void removeHandler(AbstractHandler handler);
	String getName();
}
