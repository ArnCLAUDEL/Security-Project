package session.client;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.Future;

import org.bouncycastle.cert.X509CertificateHolder;

public interface ISessionApplicant {
	Future<SessionIdentifier> requestSession(String alias, X509CertificateHolder destinationCertificateHolder) throws CertificateEncodingException, KeyStoreException, IOException;
}
