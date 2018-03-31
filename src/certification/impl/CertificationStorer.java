package certification.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import certification.ICertificationStorer;

public class CertificationStorer implements ICertificationStorer {
	private final String keyStoreAlias;
	private final KeyStore store;
	
	public CertificationStorer (String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		checkProvider();
		this.keyStoreAlias = keyStoreAlias;
		this.store = initKeyStore(keyStoreAlias);
	}
	
	private void checkProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	@Override
	public boolean storeCertificate(String alias, X509CertificateHolder holder) throws CertificateException, KeyStoreException {
		X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(holder);
		store.setCertificateEntry(alias, certificate);
		try {
			save();
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public X509CertificateHolder getCertificate(String alias)
			throws KeyStoreException, CertificateEncodingException, IOException {
		Certificate certificate = store.getCertificate(alias);
		if(certificate == null)
			throw new NoSuchElementException("Certificate for alias : " + alias + " not found");
		return new X509CertificateHolder(certificate.getEncoded());
	}
	
	private KeyStore initKeyStore(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore store = KeyStore.getInstance("JKS");
		InputStream in = new FileInputStream(new File(alias));
		char[] passw = getPassw();
		store.load(in,passw);
		return store;
	}
	
	@Override
	public void save() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		File file = new File(keyStoreAlias);
		OutputStream out = new FileOutputStream(file);
		char[] passw = getPassw();
		store.store(out, passw);
		out.close();
	}
	
	
	
	
	protected char[] getPassw() {
		/*
		System.out.println("Password of the KeyStore : ");
		Scanner scanner = new Scanner(System.in);
		char[] passw = scanner.nextLine().toCharArray();
		scanner.close();
		return passw;
		*/
		return "qwerty".toCharArray();
	}
	
}
