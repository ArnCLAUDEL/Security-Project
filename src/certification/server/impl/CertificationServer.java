package certification.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.operator.OperatorCreationException;

import certification.BaseCertificationRequest;
import certification.ICertificationStorer;
import certification.client.impl.CertificationApplicant;
import certification.impl.CertificationStorer;
import certification.server.ICertificationProvider;
import certification.server.ICertificationServer;
import certification.server.ICertificationServerProtocolHandler;
import io.AbstractIOEntity;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.server.ISessionProvider;
import session.server.ISessionServer;
import session.server.ISessionServerProtocolHandler;
import session.server.impl.ConnectedSessionServerProtocolHandler;
import session.server.impl.NotConnectedSessionServerProtocolHandler;
import session.server.impl.SessionProvider;
import util.Cheat;
import util.KeyGenerator;
import util.ProviderChecker;

public class CertificationServer extends AbstractIOEntity implements ICertificationServer, ISessionServer {
	private final KeyPair keys;
	private final ICertificationStorer storer;
	private final ICertificationProvider provider;
	private final ISessionProvider sessionProvider;
	private final Cipher privateRSACipher;
	private final String name;

	private ICertificationServerProtocolHandler certificationProtocolHandler;
	private ISessionServerProtocolHandler sessionProtocolHandler;
	private CertificationServerNetworkHandler networkHandler;
	private boolean active;
		
	public CertificationServer(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeyException, NoSuchPaddingException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException {
		super(name);
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.generateKeyPair();
		privateRSACipher = Cipher.getInstance("RSA");
		privateRSACipher.init(Cipher.PRIVATE_KEY, this.keys.getPrivate());
		this.storer = new CertificationStorer(keyStoreAlias);
		this.provider = new CertificationProvider(name, keys);
		this.certificationProtocolHandler = new NotConnectedCertificationServerProtocolHandler();
		this.sessionProvider = new SessionProvider();
		this.sessionProtocolHandler = new NotConnectedSessionServerProtocolHandler(sessionProvider, storer);
		putSelfSignedCertificate();
		this.active = false;
	}
	
	private void putSelfSignedCertificate() throws CertificateException, KeyStoreException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		X509EncodedKeySpec x509spec = new X509EncodedKeySpec(keys.getPublic().getEncoded());
		BCRSAPublicKey pubKey = (BCRSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(x509spec);
		BaseCertificationRequest request = new BaseCertificationRequest(name, new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent()));
		new CertificationApplicant().saveCSR(request, "request_ca");
		storer.storeCertificate(name, provider.validateCSR(provider.loadCSR("request_ca")));
		
	}
	
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	protected void start() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.bind(new InetSocketAddress(8888));
		networkHandler = new CertificationServerNetworkHandler(channel, this, privateRSACipher);
		active = true;
		addHandler(networkHandler);
		sessionProtocolHandler = new ConnectedSessionServerProtocolHandler(networkHandler, sessionProvider, storer);
		certificationProtocolHandler = new ConnectedCertificationServerProtocolHandler(networkHandler, provider, storer);
	}

	public void handleSessionRequest(SocketAddress from, SessionRequest request) {
		sessionProtocolHandler.handleSessionRequest(from, request);
	}

	public void sendSessionreply(SocketAddress to, SessionReply reply, Cipher cipher) {
		sessionProtocolHandler.sendSessionreply(to, reply, cipher);
	}

	@Override
	public void handleAuthRequest(SocketAddress from, AuthRequest request) {
		certificationProtocolHandler.handleAuthRequest(from, request);
	}

	@Override
	public void handleCertRequest(SocketAddress from, CertRequest request) {
		certificationProtocolHandler.handleCertRequest(from, request);
	}

	@Override
	public void sendAuthReply(SocketAddress to, AuthReply reply) {
		certificationProtocolHandler.sendAuthReply(to, reply);
	}

	@Override
	public void sendCertReply(SocketAddress to, CertReply reply) {
		certificationProtocolHandler.sendCertReply(to, reply);
	}

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		CertificationServer ca = new CertificationServer("Root-Certification-Authority", "store");
		new Thread(ca).start();
	}
}
