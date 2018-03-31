import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.cert.X509CertificateHolder;

import certification.ICertificationStorer;
import certification.impl.CertificationStorer;
import protocol.Nonce;
import protocol.message.session.SessionRequest;
import util.Cheat;
import util.KeyGenerator;
import util.ProviderChecker;
import util.SerializerBuffer;

public class Test {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, KeyStoreException, CertificateException, IOException, InvalidKeySpecException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		ProviderChecker.checkProvider();
		
		Nonce senderNonce = Nonce.generate();
		String senderAlias = "arnaud";
		String destinationAlias = "service-1";
		
		SessionRequest request = new SessionRequest(Cheat.getId(), senderNonce, senderAlias, destinationAlias);
		
		ICertificationStorer storer = new CertificationStorer("store");
		X509CertificateHolder senderCertificate = storer.getCertificate(senderAlias);
		X509CertificateHolder destinationCertificate = storer.getCertificate(destinationAlias);
		SecretKey sKey = util.KeyGenerator.generateSecretKey(128);
		
		
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(senderCertificate.getSubjectPublicKeyInfo().getEncoded());
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA", "BC");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

                KeyPair keysA = KeyGenerator.generateKeyPair();
        KeyPair keysB = KeyGenerator.generateKeyPair();
        
        System.out.println("Before");
        System.out.println(senderNonce.getValue());
        System.out.println(destinationAlias);
        for(byte b : sKey.getEncoded()) {
        	System.out.print(b);
        }
        
        
        SerializerBuffer serializer = new SerializerBuffer(2048);
        ByteBuffer encryptedSymmetricKey = ByteBuffer.allocate(2048);
        ByteBuffer encryptedForA = ByteBuffer.allocate(2048);
        ByteBuffer encryptedForB = ByteBuffer.allocate(2048);
        ByteBuffer clearB = ByteBuffer.allocate(2048);
		Cipher rsaCipher = Cipher.getInstance("RSA");
		Cipher aesCipher = Cipher.getInstance("AES");
		
		// Server
		
		rsaCipher.init(Cipher.PUBLIC_KEY, keysB.getPublic());
		serializer.putString(senderAlias);
		serializer.putInt(sKey.getEncoded().length);
		serializer.put(sKey.getEncoded());
		serializer.flip();
		rsaCipher.doFinal(serializer.getBuffer(), encryptedForB);
		encryptedForB.flip();
		serializer.clear();
		
		rsaCipher.init(Cipher.PUBLIC_KEY, keysA.getPublic());
		serializer.putInt(sKey.getEncoded().length);
		serializer.put(sKey.getEncoded());
		serializer.flip();
		rsaCipher.doFinal(serializer.getBuffer(), encryptedSymmetricKey);
		encryptedSymmetricKey.flip();
		serializer.clear();
		
		aesCipher.init(Cipher.ENCRYPT_MODE, sKey);
		senderNonce.writeToBuff(serializer);
		serializer.putString(destinationAlias);
		serializer.putInt(encryptedForB.remaining());
		serializer.put(new SerializerBuffer(encryptedForB));
		serializer.flip();
		System.err.println(serializer.remaining());
		aesCipher.doFinal(serializer.getBuffer(), encryptedForA);
		encryptedForA.flip();
		serializer.clear();
		
		// Client
		
		rsaCipher.init(Cipher.PRIVATE_KEY, keysA.getPrivate());
		rsaCipher.doFinal(encryptedSymmetricKey, serializer.getBuffer());
		serializer.flip();
		int length;
		length = serializer.getInt();
		byte[] sKeyEncoded = new byte[length];
		serializer.get(sKeyEncoded);
		serializer.clear();
		SecretKey sKdecoded = new SecretKeySpec(sKeyEncoded, "AES");
		
		System.out.println();
		for(byte b : sKdecoded.getEncoded()) {
        	System.out.print(b);
        }
		
		aesCipher.init(Cipher.DECRYPT_MODE, sKdecoded);
		
		aesCipher.doFinal(encryptedForA, serializer.getBuffer());
		serializer.flip();
		System.err.println(serializer.remaining());
		
		
		Nonce senderNonce2 = Nonce.CREATOR.init();
		senderNonce2.readFromBuff(serializer);
		String destinationAlias2 = serializer.getString();
		
		length = serializer.getInt();
		//byte[] encryptedForB2 = new byte[length];
		//serializer.get(encryptedForB2);
		
		rsaCipher.init(Cipher.PRIVATE_KEY, keysB.getPrivate());
		
		
		rsaCipher.doFinal(serializer.getBuffer(), clearB);
		clearB.flip();
		
		System.out.println();
		System.out.println("After");
        System.out.println(senderNonce2.getValue());
        System.out.println(destinationAlias2);
        for(byte b : sKeyEncoded) {
        	System.out.print(b);
        }
        System.out.println();

        System.err.println(clearB);
        
        SerializerBuffer clearBSerial = new SerializerBuffer(clearB);
        
        String senderAliasB = clearBSerial.getString();
        System.err.println(clearB);
        length = clearB.getInt();
		byte[] sKeyEncoded2 = new byte[length];
		clearB.get(sKeyEncoded2);
		
		System.out.println();
		System.out.println("B");
        System.out.println(senderAliasB);
        for(byte b : sKeyEncoded2) {
        	System.out.print(b);
        }
        System.out.println();
        
        
        
		
		
		
		
		
	}
}
