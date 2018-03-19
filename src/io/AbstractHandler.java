package io;

import java.util.logging.Level;

import util.Cheat;

public abstract class AbstractHandler implements Runnable {
	
	public AbstractHandler() {
		
	}

	protected abstract boolean stop();
	protected abstract void handle();
	public abstract void shutdown();
	
	@Override
	public void run() {
		Cheat.LOGGER.log(Level.INFO, this + " starting.");
		while(!stop()) {
			handle();
		}
		Cheat.LOGGER.log(Level.INFO, this + " shutting down.");
	}
		
	
}