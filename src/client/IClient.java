package client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Future;

import javax.crypto.NoSuchPaddingException;

import certification.client.ICertificationClient;
import certification.client.ICertificationClientProtocolHandler;
import io.IOEntity;
import protocol.NonceGenerator;
import session.client.ISessionApplicant;
import session.client.ISessionClientProtocolHandler;
import session.client.ISessionManager;
import session.client.SessionIdentifier;

public interface IClient extends NonceGenerator, IClientProtocolHandler, ICertificationClientProtocolHandler, ISessionClientProtocolHandler, ISessionManager, ISessionApplicant, IOEntity, ICertificationClient {
	void writeTo(String filename, String content, SessionIdentifier sessionIdentifier);
	Future<String> readFrom(String filename, SessionIdentifier sessionIdentifier) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException;
}
