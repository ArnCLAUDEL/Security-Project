package certification.client;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Future;

import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.cert.X509CertificateHolder;

public interface ICertificationClient {
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, CertificateEncodingException, KeyStoreException, IOException, NoSuchPaddingException, InvalidKeyException;
	public Future<X509CertificateHolder> retrieveCertificate(String alias) throws CertificateEncodingException, KeyStoreException, IOException;
}
