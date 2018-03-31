package service;

import io.IOEntity;
import protocol.NetworkWriter;
import protocol.NonceGenerator;

public interface IServiceProvider extends NonceGenerator, IOEntity, NetworkWriter {
	
}
