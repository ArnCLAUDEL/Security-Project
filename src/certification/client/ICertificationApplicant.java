package certification.client;

import java.security.spec.RSAPublicKeySpec;

import certification.BaseCertificationRequest;

/**
 * {@code ICertificationApplicant} interface represents an object that can make a
 * certification request.<br />
 * A certification request is made by providing the subject and the subject's {@link RSAPublicKeySpec}.
 * It is represented by {@link BaseCertificationRequest}.<br />
 * This {@code BaseCertificationRequest} can be written to a file. The applicant
 * should then notify a certification authority in order to validate this request. 
 */
public interface ICertificationApplicant {
	
	/**
	 * Makes a {@link BaseCertificationRequest} with the given subject and
	 * subject's {@link RSAPublicKeySpec}.
	 * @param subject The subject to put in the request
	 * @param pubKeySpec The {@code RSAPublicKeySpec} to put in the request
	 * @return A {@code BaseCertificationRequest} that contains the given parameters
	 */
	BaseCertificationRequest makeRequest(String subject, RSAPublicKeySpec pubKeySpec);
	
	/**
	 * Saves the {@link BaseCertificationRequest} to the given file.
	 * @param request The request to save
	 * @param filename The file to write to
	 * @return <tt>true</tt> if no error occurs
	 */
	boolean saveCSR(BaseCertificationRequest request, String filename);
}
