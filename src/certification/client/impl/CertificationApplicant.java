package certification.client.impl;

import java.io.IOException;
import java.security.spec.RSAPublicKeySpec;

import certification.BaseCertificationRequest;
import certification.client.ICertificationApplicant;

public class CertificationApplicant implements ICertificationApplicant {
	
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
