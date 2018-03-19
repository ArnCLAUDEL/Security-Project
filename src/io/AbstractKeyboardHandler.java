package io;

import java.nio.channels.Channels;

public abstract class AbstractKeyboardHandler extends AbstractRawInputHandler{
	
	public AbstractKeyboardHandler() {
		super(Channels.newChannel(System.in));
	}
	
	@Override
	public String toString() {
		return "Keyboard Handler " + Thread.currentThread().getId();
	}

}