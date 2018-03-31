package certification.client.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import certification.client.ICertificationClient;
import certification.impl.CertificationStorer;
import io.AbstractIOEntity;
import protocol.Nonce;
import protocol.NonceGenerator;
import util.KeyGenerator;
import util.ProviderChecker;

public abstract class ACertificationClient extends AbstractIOEntity implements ICertificationClient, NonceGenerator {
	protected final String name;
	protected final KeyPair keys;
	protected final CertificationStorer storer;
	protected final CertificationApplicant applicant;
	protected final SocketAddress localAddress;
	protected final SocketAddress caAddress;
	
	protected boolean active;
	
	public ACertificationClient(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		super();
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.generateKeyPair();
		this.caAddress = caAddress;
		this.localAddress = localAddress;
		this.storer = new CertificationStorer(keyStoreAlias);
		this.applicant = new CertificationApplicant();
		this.active = false;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public Nonce generateNonce() {
		return Nonce.generate();
	}
	
}
