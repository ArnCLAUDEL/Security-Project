package client;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import certificate.CertificationStorer;
import protocol.NonceGenerator;

public interface Client extends NonceGenerator, CertificationStorer {
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException;
}
