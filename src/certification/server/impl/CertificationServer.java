package certification.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

import javax.crypto.Cipher;

import org.bouncycastle.operator.OperatorCreationException;

import certification.ICertificationStorer;
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

	private ICertificationServerProtocolHandler certificationProtocolHandler;
	private ISessionServerProtocolHandler sessionProtocolHandler;
	private CertificationServerNetworkHandler networkHandler;
	private boolean active;
		
	public CertificationServer(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException {
		super();
		ProviderChecker.checkProvider();
		this.keys = KeyGenerator.generateKeyPair();
		this.storer = new CertificationStorer(keyStoreAlias);
		this.provider = new CertificationProvider(name, keys);
		this.certificationProtocolHandler = new NotConnectedCertificationServerProtocolHandler();
		this.sessionProvider = new SessionProvider();
		this.sessionProtocolHandler = new NotConnectedSessionServerProtocolHandler(sessionProvider, storer);
		this.active = false;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	protected void start() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.bind(new InetSocketAddress(8888));
		networkHandler = new CertificationServerNetworkHandler(channel, this);
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

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		CertificationServer ca = new CertificationServer("Root Certification Authority", "store");
		new Thread(ca).start();
	}
}
