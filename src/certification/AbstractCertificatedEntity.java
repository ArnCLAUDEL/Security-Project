package certification;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import io.AbstractIOEntity;
import protocol.Nonce;
import protocol.NonceGenerator;
import util.KeyGenerator;
import util.ProviderChecker;

public abstract class AbstractCertificatedEntity extends AbstractIOEntity implements CertificatedEntity, NonceGenerator {
	protected final String name;
	protected final KeyPair keys;
	protected final SimpleCertificationStorer storer;
	protected final SimpleCertificationApplicant applicant;
	protected final SocketAddress localAddress;
	protected final SocketAddress caAddress;
	
	protected boolean active;
	
	public AbstractCertificatedEntity(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		super();
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.randomKeys();
		this.caAddress = caAddress;
		this.localAddress = localAddress;
		this.storer = new SimpleCertificationStorer(keyStoreAlias);
		this.applicant = new SimpleCertificationApplicant();
		this.active = false;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public Nonce generateNonce() {
		return new Nonce();
	}
	
}
