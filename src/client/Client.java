package client;

import certification.CertificatedEntity;
import certification.CertificationProtocolHandler;
import io.IOEntity;
import protocol.NonceGenerator;

public interface Client extends NonceGenerator, ClientProtocolHandler, CertificationProtocolHandler, IOEntity, CertificatedEntity {
	void writeTo(String filename, String content);
	void readFrom(String filename);
}
