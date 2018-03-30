package certification;

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

import org.bouncycastle.operator.OperatorCreationException;

import io.AbstractIOEntity;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import util.Cheat;
import util.KeyGenerator;
import util.ProviderChecker;
import util.SerializerBuffer;

public class SimpleCertificationAuthorithy extends AbstractIOEntity implements CertificationAuthority {
	private final KeyPair keys;
	private final CertificationStorer storer;
	private final CertificationProvider provider;
	private final CAProtocolHandler protocolHandler;
	
	private CANetworkHandler networkHandler;
	private boolean active;
		
	public SimpleCertificationAuthorithy(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException {
		super();
		ProviderChecker.checkProvider();
		this.keys = KeyGenerator.randomKeys();
		this.storer = new SimpleCertificationStorer(keyStoreAlias);
		this.provider = new SimpleCertificationProvider(name, keys);
		this.protocolHandler = new SimpleCAProtocolHandler(this, provider, storer);
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
		networkHandler = new CANetworkHandler(channel, this);
		addHandler(networkHandler);
		active = true;
	}

	@Override
	public void handleAuthRequest(SocketAddress from, AuthRequest request) {
		protocolHandler.handleAuthRequest(from, request);
	}

	@Override
	public void handleCertRequest(SocketAddress from, CertRequest request) {
		protocolHandler.handleCertRequest(from, request);
	}

	@Override
	public void sendAuthReply(SocketAddress to, AuthReply reply) {
		protocolHandler.sendAuthReply(to, reply);
	}

	@Override
	public void sendCertReply(SocketAddress to, CertReply reply) {
		protocolHandler.sendCertReply(to, reply);
	}
	
	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return networkHandler.write(address, buffer);
	}

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, OperatorCreationException, ClassNotFoundException, InvalidKeySpecException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		SimpleCertificationAuthorithy ca = new SimpleCertificationAuthorithy("Root Certification Authority", "store");
		new Thread(ca).start();
	}
}
