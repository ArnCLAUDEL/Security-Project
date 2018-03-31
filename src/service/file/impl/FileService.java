package service.file.impl;

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
import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.BaseCertificationRequest;
import certification.client.ICertificationClientProtocolHandler;
import certification.client.impl.ACertificationClient;
import certification.client.impl.ConnectedCertificationClientProtocolHandler;
import certification.client.impl.NotConnectedCertificationClientProtocolHandler;
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
import service.file.IFileService;
import service.file.IFileServiceProtocolHandler;
import service.file.IFileServiceProvider;
import session.client.ISessionClientProtocolHandler;
import session.client.SessionInfo;
import session.client.impl.ConnectedSessionClientProtocolHandler;
import session.client.impl.NotConnectedSessionClientProtocolHandler;
import util.Cheat;

public class FileService extends ACertificationClient implements IFileService {

	private final IFileServiceProvider fileServiceProvider;
	private final Cipher privateRSACipher;
	
	private ISessionClientProtocolHandler sessionProtocolHandler;
	private ICertificationClientProtocolHandler certificationProtocolHandler;
	private IFileServiceProtocolHandler fileServiceProtocolHandler;
	private FileServiceTCPNetworkHandler networkHandlerTCP;
	private FileServiceUDPNetworkHandler networkHandlerUDP;
		
	public FileService(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress) throws NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeyException, NoSuchPaddingException {
		super(name, keyStoreAlias, localAddress, caAddress);
		this.fileServiceProvider = new FileServiceProvider();
		this.sessionProtocolHandler = new NotConnectedSessionClientProtocolHandler();
		this.fileServiceProtocolHandler = new NotConnectedFileServiceProtocolHandler(storer, this, fileServiceProvider);
		this.certificationProtocolHandler = new NotConnectedCertificationClientProtocolHandler(storer);
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
	protected void start() throws IOException {
		active = true;
		DatagramChannel datagramChannel = DatagramChannel.open();
		datagramChannel.bind(localAddress);
		networkHandlerUDP = new FileServiceUDPNetworkHandler(datagramChannel, this, privateRSACipher);
		addHandler(networkHandlerUDP);
		certificationProtocolHandler = new ConnectedCertificationClientProtocolHandler(networkHandlerUDP, storer);
		sessionProtocolHandler = new ConnectedSessionClientProtocolHandler(networkHandlerUDP, storer, sessionManager);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(localAddress);
		networkHandlerTCP = new FileServiceTCPNetworkHandler(serverSocketChannel, this, privateRSACipher);
		addHandler(networkHandlerTCP);
		fileServiceProtocolHandler = new ConnectedFileServiceProtocolHandler(this, storer, fileServiceProvider, networkHandlerTCP);
	}
	
	@Override
	public boolean createSession(long id, SessionInfo info) {
		return sessionManager.createSession(id, info);
	}
	
	@Override
	public SessionInfo getSessionInfo(long id) {
		return sessionManager.getSessionInfo(id);
	}
	
	@Override
	public void sendSessionRequest(SocketAddress to, SessionRequest request, SessionInfo info) {
		sessionProtocolHandler.sendSessionRequest(to, request, info);
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
	public void handleServiceFileRead(SocketAddress from, ServiceFileReadRequest request) {
		fileServiceProtocolHandler.handleServiceFileRead(from, request);
	}

	@Override
	public void handleServiceFileWrite(SocketAddress from, ServiceFileWriteRequest request) {
		fileServiceProtocolHandler.handleServiceFileWrite(from, request);
	}

	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadReply reply) {
		fileServiceProtocolHandler.sendServiceFileRead(to, reply);
	}

	@Override
	public void sendServiceFileWrite(SocketAddress to, ServiceFileWriteReply reply) {
		fileServiceProtocolHandler.sendServiceFileWrite(to, reply);
	}
	
	public static void main(String[] args) throws NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		FileService service = new FileService("Service-1", "store_service1", new InetSocketAddress(8890), new InetSocketAddress(8888));
		service.start();
		service.makeCertificationRequest();
	}
}
