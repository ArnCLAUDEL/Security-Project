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
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.BaseCertificationRequest;
import certification.client.ICertificationClientProtocolHandler;
import certification.client.impl.ACertificationClient;
import certification.client.impl.ConnectedCertificationClientProtocolHandler;
import certification.client.impl.NotConnectedCertificationClientProtocolHandler;
import client.IClient;
import client.IClientProtocolHandler;
import io.AbstractKeyboardHandler;
import io.Handler;
import protocol.Nonce;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import protocol.message.session.SessionAck;
import protocol.message.session.SessionInit;
import protocol.message.session.SessionOk;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.client.ISessionClientProtocolHandler;
import session.client.SessionIdentifier;
import session.client.SessionInfo;
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
		sendAuthRequest(caAddress, new AuthRequest(Cheat.getId(), filename, name));
	}
	
	@Override
	public Future<X509CertificateHolder> retrieveCertificate(String alias) throws CertificateEncodingException, KeyStoreException, IOException {
		try {
			X509CertificateHolder holder = storer.getCertificate(alias);
			return CompletableFuture.completedFuture(holder);
		} catch (NoSuchElementException e) {
			return sendCertRequest(caAddress, new CertRequest(Cheat.getId(), alias));
		}		
	}
	
	@Override
	public void writeTo(String filename, String content, SessionIdentifier sessionIdentifier) {
		sendServiceFileWrite(fileServiceAddress, new ServiceFileWriteRequest(Cheat.getId(), filename, content, sessionIdentifier));
	}
	
	@Override
	public Future<String> readFrom(String filename, SessionIdentifier sessionIdentifier) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		System.out.println("Sending request..");
		return sendServiceFileRead(fileServiceAddress, new ServiceFileReadRequest(Cheat.getId(), filename, sessionIdentifier), sessionManager.getSessionInfo(sessionIdentifier.getId()));
	}
	
	@Override
	public Future<SessionIdentifier> requestSession(String alias, X509CertificateHolder destinationCertificateHolder) throws CertificateEncodingException, KeyStoreException, IOException {
		SessionRequest request = new SessionRequest(Cheat.getId(), Nonce.generate(), name, alias);
		SessionInfo info = new SessionInfo(request, fileServiceAddress, destinationCertificateHolder);
		return sendSessionRequest(sessionServerAddress, request, info);
	}
	
	@Override
	public SessionInfo getSessionInfo(long id) {
		return sessionManager.getSessionInfo(id);
	}

	@Override
	public boolean createSession(long id, SessionInfo info) {
		return sessionManager.createSession(id, info);
	}
	
	@Override
	public boolean checkSessionIdentifier(SessionIdentifier sessionIdentifier) {
		return sessionManager.checkSessionIdentifier(sessionIdentifier);
	}
	
	@Override
	public Future<SessionIdentifier> sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info) {
		return sessionProtocolHandler.sendSessionRequest(to, request, info);
	}

	@Override
	public void handleSessionReply(SocketAddress from, SessionReply reply) {
		sessionProtocolHandler.handleSessionReply(from, reply);
	}
	
	@Override
	public void sendSessionInit(SocketAddress to, SessionInit init, Cipher cipher) {
		sessionProtocolHandler.sendSessionInit(to, init, cipher);
	}

	@Override
	public void handleSessionInit(SocketAddress from, SessionInit init) {
		sessionProtocolHandler.handleSessionInit(from, init);
	}

	@Override
	public void sendSessionAck(SocketAddress to, SessionAck ack, Cipher cipher) {
		sessionProtocolHandler.sendSessionAck(to, ack, cipher);
	}

	@Override
	public void handleSessionAck(SocketAddress from, SessionAck ack) {
		sessionProtocolHandler.handleSessionAck(from, ack);
	}

	@Override
	public void sendSessionOk(SocketAddress to, SessionOk ok, Cipher cipher) {
		sessionProtocolHandler.sendSessionOk(to, ok, cipher);
	}

	@Override
	public void handleSessionOk(SocketAddress from, SessionOk ok) {
		sessionProtocolHandler.handleSessionOk(from, ok);
	}

	@Override
	public CompletableFuture<String> sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request, SessionInfo info) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		return clientProtocolHandler.sendServiceFileRead(to, request, info);
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
	public Future<X509CertificateHolder> sendAuthRequest(SocketAddress to, AuthRequest request) {
		return certificationProtocolHandler.sendAuthRequest(to, request);
	}

	@Override
	public Future<X509CertificateHolder> sendCertRequest(SocketAddress to, CertRequest request) {
		return certificationProtocolHandler.sendCertRequest(to, request);
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
		sessionProtocolHandler = new ConnectedSessionClientProtocolHandler(networkHandlerUDP, storer, sessionManager);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(localAddress);
		networkHandlerTCP = new ClientTCPNetworkHandler(serverSocketChannel, this, privateRSACipher);
		addHandler(networkHandlerTCP);
		clientProtocolHandler = new ConnectedClientProtocolHandler(networkHandlerTCP);
		final IClient client = this;
		
		networkHandlerTCP.connect(fileServiceAddress);
		
		Handler handler = new AbstractKeyboardHandler() {
			@Override
			protected void handle(SerializerBuffer serializerBuffer) throws IOException {
				try {
					System.out.println("Preparing request..");
					Future<X509CertificateHolder> futureHolder = client.retrieveCertificate("service-1");
					System.out.println("Waiting for CertificateHolder..");
					X509CertificateHolder holder = futureHolder.get();
					System.out.println("CertificateHolder received..");
					Future<SessionIdentifier> futureSessionIdentifier = client.requestSession("service-1", holder);
					System.out.println("Waiting for SessionIdentifier..");
					SessionIdentifier sessionIdentifier = futureSessionIdentifier.get();
					System.out.println("SessionIdentifier received..");
					try {Thread.sleep(1000);}
					catch (InterruptedException e) {}
					Future<String> futureResult = readFrom("test.txt", sessionIdentifier);
					System.out.println("Waiting for result..");
					String result = futureResult.get();
					System.out.println("Result received : " + result);
				} catch (InterruptedException | ExecutionException | CertificateEncodingException | KeyStoreException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
					Cheat.LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}
		};
		addHandler(handler);
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		IClient client = new Client("arnaud", "store_client", new InetSocketAddress(8889), new InetSocketAddress(8888), new InetSocketAddress(8890), new InetSocketAddress(8888));
		new Thread(client).start();
		client.makeCertificationRequest();
	}	
}
