package client;

import certification.client.ICertificationClient;
import certification.client.ICertificationClientProtocolHandler;
import io.IOEntity;
import protocol.NonceGenerator;
import session.client.ISessionApplicant;
import session.client.ISessionClientProtocolHandler;
import session.client.ISessionManager;

public interface IClient extends NonceGenerator, IClientProtocolHandler, ICertificationClientProtocolHandler, ISessionClientProtocolHandler, ISessionManager, ISessionApplicant, IOEntity, ICertificationClient {
	void writeTo(String filename, String content);
	void readFrom(String filename);
}
