package session.client;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;

import javax.crypto.SecretKey;

public interface ISessionApplicant {
	SecretKey requestSessionKey() throws CertificateEncodingException, KeyStoreException, IOException;
}
