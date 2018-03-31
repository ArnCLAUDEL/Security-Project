package certification.client;

import java.security.spec.RSAPublicKeySpec;

import certification.BaseCertificationRequest;

public interface ICertificationApplicant {
	BaseCertificationRequest makeRequest(String subject, RSAPublicKeySpec pubKeySpec);
	boolean saveCSR(BaseCertificationRequest request, String filename);
}
