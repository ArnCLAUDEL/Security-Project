package certificate;

import java.io.IOException;
import java.security.spec.RSAPublicKeySpec;

public class SimpleCertificationApplicant implements CertificationApplicant {
	
	@Override
	public boolean saveCSR(BaseCertificationRequest request, String filename) {
		try {
			request.writeTo(filename);
			return true;
		} catch (IOException e) { 
			return false;
		}
	}
	
	@Override
	public BaseCertificationRequest makeRequest(String subject, RSAPublicKeySpec pubKeySpec) {
		return new BaseCertificationRequest(subject, pubKeySpec);
	}

}
