package session.server;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

/**
 * {@code ISessionProvider} interface provides a way to generate
 * a {@link SecretKey}.<br />
 * This can be used to generate a new session key.
 */
public interface ISessionProvider {
	
	/**
	 * Generates a new session {@link SecretKey}.
	 * @return The session key
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	SecretKey generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException; 
}
