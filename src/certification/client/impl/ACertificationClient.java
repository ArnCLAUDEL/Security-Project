package certification.client.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import certification.ICertificationStorer;
import certification.client.ICertificationApplicant;
import certification.client.ICertificationClient;
import certification.impl.CertificationStorer;
import io.AbstractIOEntity;
import protocol.Nonce;
import protocol.NonceGenerator;
import session.client.ISessionManager;
import session.client.impl.SessionManager;
import util.KeyGenerator;
import util.ProviderChecker;

public abstract class ACertificationClient extends AbstractIOEntity implements ICertificationClient, NonceGenerator {
	protected final String name;
	protected final KeyPair keys;
	protected final ISessionManager sessionManager;
	protected final ICertificationStorer storer;
	protected final ICertificationApplicant applicant;
	protected final SocketAddress localAddress;
	protected final SocketAddress caAddress;
	protected final String certificationServerAlias;
	protected final Cipher privateRSACipher;
	
	protected boolean active;
	
	public ACertificationClient(String name, String keyStoreAlias, String certificationServerAlias, SocketAddress localAddress, SocketAddress caAddress) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException {
		super();
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.generateKeyPair();
		this.privateRSACipher = Cipher.getInstance("RSA");
		this.privateRSACipher.init(Cipher.PRIVATE_KEY, keys.getPrivate());
		this.caAddress = caAddress;
		this.localAddress = localAddress;
		this.sessionManager = new SessionManager();
		this.storer = new CertificationStorer(keyStoreAlias);
		this.applicant = new CertificationApplicant();
		this.certificationServerAlias = certificationServerAlias;
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
