package certification;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.NoSuchElementException;

import org.bouncycastle.cert.X509CertificateHolder;

/**
 * {@code ICertificationStorer} interface provides a way to store/load certificates
 * to/from a {@link KeyStore}. The KeyStore can also be saved to a keystore file.<br />
 * A Certificate can be retrieved by giving its alias and it will be returned
 * as a {@link X509CertificateHolder}.
 */
public interface ICertificationStorer {
	
	/**
	 * Stores the certificate contained in the given {@link X509CertificateHolder} to the {@link KeyStore} with its alias. <br />
	 * The {@code KeyStore} is not necessarly saved to the keystore file after this operation. 
	 * @param alias the alias to store with the certificate
	 * @param holder the certificate holder to store.
	 * @return <tt>true</tt> if the certificate has been successfuly stored
	 * @throws CertificateException
	 * @throws KeyStoreException
	 */
	boolean storeCertificate(String alias, X509CertificateHolder holder) throws CertificateException, KeyStoreException;
	
	/**
	 * Retrieves the certificate stored in the {@link KeyStore} with the given alias.<br />
	 * @param alias The alias of the certificate to retrieve
	 * @return The certificate holder that contains the certificate 
	 * @throws KeyStoreException
	 * @throws CertificateEncodingException
	 * @throws IOException
	 * @throws NoSuchElementException If the certificate is not in the {@code KeyStore}
	 */
	X509CertificateHolder getCertificate(String alias) throws KeyStoreException, CertificateEncodingException, IOException, NoSuchElementException;
	
	/**
	 * Save the current {@link KeyStore} in a keystore file.
	 * The file used is the same as the one used when loading the {@code KeyStore}.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	void save() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException;
}
