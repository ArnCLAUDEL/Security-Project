package certification;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import util.Cheat;

public class SimpleCAProtocolHandler extends AbstractProtocolHandler implements CAProtocolHandler {
	
	private final CertificationProvider provider;
	private final CertificationStorer storer;
	
	public SimpleCAProtocolHandler(NetworkWriter networkWriter, CertificationProvider provider, CertificationStorer storer) {
		super(networkWriter);	
		this.provider = provider;
		this.storer = storer;
	}
	
	@Override
	public void handleAuthRequest(SocketAddress from, AuthRequest request) {
		try {
			PKCS10CertificationRequest csr = provider.loadCSR(request.getFilename());
			X509CertificateHolder holder = provider.validateCSR(csr);
			storer.storeCertificate(request.getAlias(), holder);
			storer.save();
			Cheat.LOGGER.log(Level.INFO, "Certificate stored with alias : " + request.getAlias());
			sendAuthReply(from, new AuthReply(request.getAlias(), holder));
		} catch (IOException | ClassNotFoundException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | OperatorCreationException | CertificateException | KeyStoreException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void handleCertRequest(SocketAddress from, CertRequest request) {
		try {
			X509CertificateHolder holder = storer.getCertificate(request.getAlias());
			
			sendCertReply(from, new CertReply(request.getAlias(), holder));
		} catch (CertificateEncodingException | KeyStoreException | IOException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		} catch (NoSuchElementException e) {
			Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void sendAuthReply(SocketAddress to, AuthReply reply) {
		send(to, reply);
	}

	@Override
	public void sendCertReply(SocketAddress to, CertReply reply) {
		send(to, reply);
		
	}

}
