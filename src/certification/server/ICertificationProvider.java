package certification.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public interface ICertificationProvider {
	PKCS10CertificationRequest loadCSR(String filename) throws IOException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException;
	X509CertificateHolder validateCSR(PKCS10CertificationRequest csr) throws OperatorCreationException;
}