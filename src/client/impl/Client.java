package client.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.BaseCertificationRequest;
import certification.client.ICertificationClientProtocolHandler;
import certification.client.impl.ACertificationClient;
import certification.client.impl.ConnectedCertificationClientProtocolHandler;
import certification.client.impl.NotConnectedCertificationClientProtocolHandler;
import client.IClient;
import client.IClientProtocolHandler;
import io.AbstractKeyboardHandler;
import protocol.Nonce;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.ISessionClientProtocolHandler;
import session.client.impl.ConnectedSessionClientProtocolHandler;
import session.client.impl.NotConnectedSessionClientProtocolHandler;
import util.Cheat;
import util.SerializerBuffer;

public class Client extends ACertificationClient implements IClient {
	private final SocketAddress fileServiceAddress;
	private final SocketAddress sessionServerAddress;
	private final Cipher privateRSACipher;
	
	private ISessionClientProtocolHandler sessionProtocolHandler;
	private IClientProtocolHandler clientProtocolHandler;
	private ICertificationClientProtocolHandler certificationProtocolHandler;
	private ClientUDPNetworkHandler networkHandlerUDP;
	private ClientTCPNetworkHandler networkHandlerTCP;
	
	
	public Client(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress, SocketAddress fileServiceAddress, SocketAddress sessionServerAddress) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
		super(name, keyStoreAlias, localAddress, caAddress);
		this.sessionProtocolHandler = new NotConnectedSessionClientProtocolHandler();
		this.clientProtocolHandler = new NotConnectedClientProtocolHandler();
		this.certificationProtocolHandler = new NotConnectedCertificationClientProtocolHandler(storer);
		this.fileServiceAddress = fileServiceAddress;
		this.sessionServerAddress = sessionServerAddress;
		this.privateRSACipher = Cipher.getInstance("RSA");
		this.privateRSACipher.init(Cipher.PRIVATE_KEY, keys.getPrivate());
	}
	
	@Override
	public void makeCertificationRequest() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		X509EncodedKeySpec x509spec = new X509EncodedKeySpec(keys.getPublic().getEncoded());
		BCRSAPublicKey pubKey = (BCRSAPublicKey) KeyFactory.getInstance("RSA", "BC").generatePublic(x509spec);
		BaseCertificationRequest request = applicant.makeRequest(name, new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent()));
		String filename = "request_" + name;
		applicant.saveCSR(request, filename);
		sendAuthRequest(caAddress, new AuthRequest(filename, name));
	}
	
	@Override
	public void retrieveCertificate(String alias) {
		sendCertRequest(caAddress, new CertRequest(alias));
	}
	
	@Override
	public void writeTo(String filename, String content) {
		sendServiceFileWrite(fileServiceAddress, new ServiceFileWriteRequest(Cheat.RANDOM.nextLong(), filename, content));
	}
	
	@Override
	public void readFrom(String filename) {
		System.out.println("Sending request..");
		Future<String> result = sendServiceFileRead(fileServiceAddress, new ServiceFileReadRequest(Cheat.RANDOM.nextLong(), filename));
		System.out.println("Waiting for result..");
		try {
			System.out.println(result.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public SecretKey requestSessionKey() {
		sendSessionRequest(sessionServerAddress, new SessionRequest(Cheat.RANDOM.nextLong(), Nonce.generate(), name, "service-1"));
		return null;
	}
	
	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request) {
		sessionProtocolHandler.sendSessionRequest(to, request);
	}

	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {
		sessionProtocolHandler.handleSessionReply(from, reply);
	}

	@Override
	public CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request) {
		return clientProtocolHandler.sendServiceFileRead(to, request);
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteRequest request) {
		clientProtocolHandler.sendServiceFileWrite(to, request);
	}

	@Override
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadReply reply) {
		clientProtocolHandler.handleServiceFileRead(from, reply);
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteReply reply) {
		clientProtocolHandler.handleServiceFileWrite(from, reply);
	}

	@Override
	public void sendAuthRequest(SocketAddress to, AuthRequest request) {
		certificationProtocolHandler.sendAuthRequest(to, request);
	}

	@Override
	public void sendCertRequest(SocketAddress to, CertRequest request) {
		certificationProtocolHandler.sendCertRequest(to, request);
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		certificationProtocolHandler.handleAuthReply(from, reply);
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		certificationProtocolHandler.handleCertReply(from, reply);
	}	

	@Override
	protected void start() throws IOException {
		active = true;
		
		DatagramChannel datagramChannel = DatagramChannel.open();
		datagramChannel.bind(localAddress);
		networkHandlerUDP = new ClientUDPNetworkHandler(datagramChannel, this, privateRSACipher);
		addHandler(networkHandlerUDP);
		certificationProtocolHandler = new ConnectedCertificationClientProtocolHandler(networkHandlerUDP, storer);
		sessionProtocolHandler = new ConnectedSessionClientProtocolHandler(networkHandlerUDP);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(localAddress);
		networkHandlerTCP = new ClientTCPNetworkHandler(serverSocketChannel, this, privateRSACipher);
		addHandler(networkHandlerTCP);
		clientProtocolHandler = new ConnectedClientProtocolHandler(networkHandlerTCP);
		final IClient client = this;
		
		//networkHandlerTCP.connect(fileServiceAddress);
		
		addHandler(new AbstractKeyboardHandler() {
			
			@Override
			protected void handle(SerializerBuffer serializerBuffer) throws IOException {
				Cheat.LOGGER.log(Level.INFO, "Preparing writing..");
				//client.writeTo("test.txt", "Hello World");
				Cheat.LOGGER.log(Level.INFO, "Writing sent..");
				try {Thread.sleep(1000);}
				catch (InterruptedException e) {}
				Cheat.LOGGER.log(Level.INFO, "");
				Cheat.LOGGER.log(Level.INFO, "Preparing reading..");
				client.requestSessionKey();
				Cheat.LOGGER.log(Level.INFO, "Reading sent..");
			}
		});
		
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		IClient client = new Client("arnaud", "store_client", new InetSocketAddress(8889), new InetSocketAddress(8888), new InetSocketAddress(8890), new InetSocketAddress(8888));
		new Thread(client).start();
		client.makeCertificationRequest();
	}
	
}
