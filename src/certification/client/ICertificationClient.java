package certification.client;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Future;

import org.bouncycastle.cert.X509CertificateHolder;

public interface ICertificationClient {
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException;
	public Future<X509CertificateHolder> retrieveCertificate(String alias);
}
