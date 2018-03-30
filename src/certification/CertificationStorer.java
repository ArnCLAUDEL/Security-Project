package certification;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import org.bouncycastle.cert.X509CertificateHolder;

public interface CertificationStorer {
	boolean storeCertificate(String alias, X509CertificateHolder holder) throws CertificateException, KeyStoreException;
	X509CertificateHolder getCertificate(String alias) throws KeyStoreException, CertificateEncodingException, IOException;
	void save() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException;
}
