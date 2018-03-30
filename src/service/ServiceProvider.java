package service;

import io.IOEntity;
import protocol.NetworkWriter;
import protocol.NonceGenerator;

public interface ServiceProvider extends NonceGenerator, IOEntity, NetworkWriter {
	
}
