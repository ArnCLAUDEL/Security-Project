package certification.server.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import certification.BaseCertificationRequest;
import certification.server.ICertificationProvider;

public class CertificationProvider implements ICertificationProvider {
	public final static Period DEFAUT_VALIDITY_PERIOD = Period.ofDays(1);
	
	private final String name;
	private final KeyPair keys;
	
	private Period period;
	
	public CertificationProvider(String name, Period period, KeyPair keys) {
		this.name = name;
		this.keys = keys;
		this.period = period;
	}
	
	public CertificationProvider(String name, KeyPair keys) {
		this.name = name;
		this.keys = keys;
		this.period = DEFAUT_VALIDITY_PERIOD;
	}
	
	@Override
	public PKCS10CertificationRequest loadCSR(String filename) throws IOException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException {
		BaseCertificationRequest bcr = BaseCertificationRequest.readFrom(filename);
		X500Name subject = new X500Name("CN=" + bcr.getSubject());
		PublicKey key = KeyFactory.getInstance("RSA", "BC").generatePublic(bcr.getPubKeySpec());
		SubjectPublicKeyInfo pubKeyInfo = SubjectPublicKeyInfo.getInstance(key.getEncoded());
		ContentSigner signer = getContentSigner();
		return new PKCS10CertificationRequestBuilder(subject, pubKeyInfo).build(signer);		
	}

	@Override
	public X509CertificateHolder validateCSR(PKCS10CertificationRequest csr) throws OperatorCreationException {
		X500Name issuerName = new X500Name("CN=" + name);
		Date notBefore = Date.from(Instant.now());
		Date notAfter = Date.from(Instant.now().plus(period));
		X500Name subjectName = csr.getSubject();
		SubjectPublicKeyInfo pubKeyInfo = csr.getSubjectPublicKeyInfo();
		BigInteger serial = getSerial();
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuerName, serial, notBefore, notAfter, subjectName, pubKeyInfo);
		ContentSigner signer = getContentSigner();
		return builder.build(signer);
	}
	
	private ContentSigner getContentSigner() throws OperatorCreationException {
		return new JcaContentSignerBuilder("SHA512withRSA").setProvider("BC").build(keys.getPrivate());
	}
	
	private BigInteger getSerial() {
		return BigInteger.valueOf(new Random().nextLong());
	}

}
