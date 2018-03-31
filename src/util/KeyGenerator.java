package util;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

public class KeyGenerator {
	
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		return generator.generateKeyPair();
	}
	
	public static SecretKey generateSecretKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		return generateSecretKey(128);
	}
	
	public static SecretKey generateSecretKey(int length) throws NoSuchAlgorithmException, NoSuchProviderException {
		javax.crypto.KeyGenerator gen = javax.crypto.KeyGenerator.getInstance("AES", "BC");
		gen.init(length);
		return gen.generateKey();
	}
	
	public static BCRSAPublicKey bcrsaPublicKeyConverter(X509CertificateHolder holder) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(holder.getSubjectPublicKeyInfo().getEncoded());
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA", "BC");
        return (BCRSAPublicKey) keyFactory.generatePublic(keySpec);
	}
}
