package client;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certificate.BaseCertificationRequest;
import certificate.SimpleCertificationApplicant;
import certificate.SimpleCertificationStorer;
import protocol.Nonce;
import util.KeyGenerator;
import util.ProviderChecker;

public class SimpleClient implements Client {
	private final String name;
	private final KeyPair keys;
	private final SimpleCertificationStorer storer;
	private final SimpleCertificationApplicant applicant;
	
	public SimpleClient(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.randomKeys();
		this.storer = new SimpleCertificationStorer(keyStoreAlias);
		this.applicant = new SimpleCertificationApplicant();
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeySpecException {
		Client client = new SimpleClient("machin", "store_client");
		client.makeCertificationRequest();
	}
	
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		X509EncodedKeySpec x509spec = new X509EncodedKeySpec(keys.getPublic().getEncoded());
		BCRSAPublicKey pubKey = (BCRSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(x509spec);
		BaseCertificationRequest request = applicant.makeRequest(name, new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent()));
		applicant.saveCSR(request, "request_" + name);
	}
	
	@Override
	public boolean storeCertificate(String alias, X509CertificateHolder holder)
			throws CertificateException, KeyStoreException {
		return storer.storeCertificate(alias, holder);
	}

	@Override
	public X509CertificateHolder getCertificate(String alias)
			throws KeyStoreException, CertificateEncodingException, IOException {
		return storer.getCertificate(alias);
	}
	
	@Override
	public void save() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		storer.save();
	}

	@Override
	public Nonce generateNonce() {
		return new Nonce();
	}			
}
