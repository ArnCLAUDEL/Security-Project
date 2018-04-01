package session.client;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.Future;

import org.bouncycastle.cert.X509CertificateHolder;

/**
 * {@code ISessionApplicant} interface provides a way to request a session
 * with the an another {@code ISessionApplicant}.<br />
 * This will request to a session's generation server a new session key that
 * will be used to further communications. 
 * The certificate of the other applicant must be known. 
 */
public interface ISessionApplicant {
	
	/**
	 * Request a new session with another applicant to a session's generation server.<br />
	 * The other applicant's certificate must be provided in order to encrypt the future 
	 * session key with its public key.<br />
	 * This method will immediatly returns a {@link Future} that will be completed upon
	 * receiving a reply from the server.
	 * @param alias The alias of the intializer applicant
	 * @param destinationCertificateHolder The certificate of the other applicant
	 * @return The future that will contains the result
	 * @throws CertificateEncodingException
	 * @throws KeyStoreException
	 * @throws IOException
	 */
	Future<SessionIdentifier> requestSession(String alias, X509CertificateHolder destinationCertificateHolder) throws CertificateEncodingException, KeyStoreException, IOException;
}
