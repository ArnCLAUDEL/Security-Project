package server.ca;

import io.IOEntity;
import protocol.NetworkWriter;

public interface CertificationAuthority extends IOEntity, CAProtocolHandler, NetworkWriter {
	
}