package server.ca;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import certificate.CertificationProvider;
import certificate.CertificationStorer;
import certificate.SimpleCertificationProvider;
import certificate.SimpleCertificationStorer;
import util.KeyGenerator;
import util.ProviderChecker;

public class SimpleCertificationAuthorithy implements CertificationAuthority {
	private final KeyPair keys;
	private final CertificationStorer storer;
	private final CertificationProvider provider;
		
	public SimpleCertificationAuthorithy(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException {
		ProviderChecker.checkProvider();
		this.keys = KeyGenerator.randomKeys();
		this.storer = new SimpleCertificationStorer(keyStoreAlias);
		this.provider = new SimpleCertificationProvider(name, keys);
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException {
		SimpleCertificationAuthorithy ca = new SimpleCertificationAuthorithy("Root Certification Authority", "store");
		PKCS10CertificationRequest request = ca.loadCSR("request_machin");
		X509CertificateHolder holder = ca.validateCSR(request);
		ca.storeCertificate("certificate", holder);
		String alias = holder.getSubject().toString();
		X509CertificateHolder newHolder = ca.getCertificate(alias);
	}

	public boolean storeCertificate(String alias, X509CertificateHolder holder)
			throws CertificateException, KeyStoreException {
		return storer.storeCertificate(alias, holder);
	}

	public X509CertificateHolder getCertificate(String alias)
			throws KeyStoreException, CertificateEncodingException, IOException {
		return storer.getCertificate(alias);
	}

	public void save() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		storer.save();
	}

	public PKCS10CertificationRequest loadCSR(String filename) throws IOException, ClassNotFoundException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
		return provider.loadCSR(filename);
	}

	public X509CertificateHolder validateCSR(PKCS10CertificationRequest csr) throws OperatorCreationException {
		return provider.validateCSR(csr);
	}		
}
