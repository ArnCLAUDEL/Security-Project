package session.server;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

public interface ISessionProvider {
	SecretKey generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException; 
}
