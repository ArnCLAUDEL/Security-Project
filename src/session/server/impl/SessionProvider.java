package session.server.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import session.server.ISessionProvider;
import util.ProviderChecker;

public class SessionProvider implements ISessionProvider {

	@Override
	public SecretKey generateSessionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		ProviderChecker.checkProvider();
		KeyGenerator gen = KeyGenerator.getInstance("AES", "BC");
		return gen.generateKey();
	}
	
}
