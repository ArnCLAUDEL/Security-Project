package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
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

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certificate.BaseCertificationRequest;
import certificate.SimpleCertificationApplicant;
import certificate.SimpleCertificationStorer;
import io.AbstractIOEntity;
import protocol.Nonce;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import util.Cheat;
import util.KeyGenerator;
import util.ProviderChecker;
import util.SerializerBuffer;

public class SimpleClient extends AbstractIOEntity implements Client {
	private final String name;
	private final KeyPair keys;
	private final SimpleCertificationStorer storer;
	private final SimpleCertificationApplicant applicant;
	private final ClientProtocolHandler protocolHandler;
	private final SocketAddress localAddress;
	
	private SocketAddress caAddress;
	
	private ClientUDPNetworkHandler networkHandler;
	private boolean active;
	
	public SimpleClient(String name, String keyStoreAlias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		super();
		ProviderChecker.checkProvider();
		this.name = name;
		this.keys = KeyGenerator.randomKeys();
		this.localAddress = new InetSocketAddress(8889);
		this.caAddress = new InetSocketAddress(8888);
		this.storer = new SimpleCertificationStorer(keyStoreAlias);
		this.applicant = new SimpleCertificationApplicant();
		this.protocolHandler = new SimpleClientProtocolHandler(this, storer, caAddress);
		this.active = false;
	}
	
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		X509EncodedKeySpec x509spec = new X509EncodedKeySpec(keys.getPublic().getEncoded());
		BCRSAPublicKey pubKey = (BCRSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(x509spec);
		BaseCertificationRequest request = applicant.makeRequest(name, new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent()));
		String filename = "request_" + name;
		applicant.saveCSR(request, filename);
		sendAuthRequest(caAddress, new AuthRequest(filename, name));
	}
	
	public void retrieveCertificate(String alias) {
		protocolHandler.sendCertRequest(caAddress, new CertRequest(alias));
	}
	
	@Override
	public Nonce generateNonce() {
		return new Nonce();
	}

	@Override
	public void sendAuthRequest(SocketAddress to, AuthRequest request) {
		protocolHandler.sendAuthRequest(to, request);
	}

	@Override
	public void sendCertRequest(SocketAddress to, CertRequest request) {
		protocolHandler.sendCertRequest(to, request);
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		protocolHandler.handleAuthReply(from, reply);
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		protocolHandler.handleCertReply(from, reply);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	protected void start() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.bind(localAddress);
		networkHandler = new ClientUDPNetworkHandler(channel, this);
		addHandler(networkHandler);
		active = true;
	}

	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return networkHandler.write(address, buffer);
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeySpecException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		Client client = new SimpleClient("arnaud", "store_client");
		new Thread(client).start();
		client.makeCertificationRequest();
		client.retrieveCertificate("arnaud");
	}
	
}
