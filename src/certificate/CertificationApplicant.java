package certificate;

import java.security.spec.RSAPublicKeySpec;

public interface CertificationApplicant {
	BaseCertificationRequest makeRequest(String subject, RSAPublicKeySpec pubKeySpec);
	boolean saveCSR(BaseCertificationRequest request, String filename);
}
