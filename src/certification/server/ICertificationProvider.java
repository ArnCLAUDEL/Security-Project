package certification.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import certification.BaseCertificationRequest;

/**
 * {@code ICertificationProvider} interface provides a way to build and validate
 * {@link PKCS10CertificationRequest}.<br />
 * The {@code PKCS10CertificationRequest} is made by loading a {@link BaseCertificationRequest}
 * contained in a file. It is then validated and converted to a {@link X509CertificateHolder}.
 */
public interface ICertificationProvider {
	
	/**
	 * Loads a {@link BaseCertificationRequest} from the given file and build a 
	 * {@link PKCS10CertificationRequest} with its informations.
	 * @param filename The file that contains the request
	 * @return the request with the informations from the given file 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws OperatorCreationException
	 */
	PKCS10CertificationRequest loadCSR(String filename) throws IOException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException;
	
	/**
	 * Validates a {@link PKCS10CertificationRequest} and converts it to a 
	 * {@link X509CertificationHolder}.
	 * @param csr The request to validate
	 * @return The certificate's holder made with the given request
	 * @throws OperatorCreationException
	 */
	X509CertificateHolder validateCSR(PKCS10CertificationRequest csr) throws OperatorCreationException;
}