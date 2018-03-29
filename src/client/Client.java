package client;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import io.IOEntity;
import protocol.NetworkWriter;
import protocol.NonceGenerator;

public interface Client extends NonceGenerator, ClientProtocolHandler, IOEntity, NetworkWriter {
	
	//TODO String makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException;
	// String -> filename of the request
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException;
	public void retrieveCertificate(String alias);
}
