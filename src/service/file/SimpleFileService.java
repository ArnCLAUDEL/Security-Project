package service.file;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.AbstractCertificatedEntity;
import certification.BaseCertificationRequest;
import certification.CertificationProtocolHandler;
import certification.ConnectedCertificationProtocolHandler;
import certification.NotConnectedCertificationProtocolHandler;
import protocol.message.AuthReply;
import protocol.message.AuthRequest;
import protocol.message.CertReply;
import protocol.message.CertRequest;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import service.file.state.ConnectedFileServiceProtocolHandler;
import service.file.state.NotConnectedFileServiceProtocolHandler;
import util.Cheat;
import util.SerializerBuffer;

public class SimpleFileService extends AbstractCertificatedEntity implements FileService {

	private final FileServiceProvider fileServiceProvider;
	
	private CertificationProtocolHandler certificationProtocolHandler;
	private FileServiceProtocolHandler fileServiceProtocolHandler;
	private FileServiceTCPNetworkHandler networkHandlerTCP;
	private FileServiceUDPNetworkHandler networkHandlerUDP;
	
	public SimpleFileService(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress) throws NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		super(name, keyStoreAlias, localAddress, caAddress);
		this.fileServiceProvider = new SimpleFileServiceProvider();
		this.fileServiceProtocolHandler = new NotConnectedFileServiceProtocolHandler(storer, this, fileServiceProvider);
		this.certificationProtocolHandler = new NotConnectedCertificationProtocolHandler(storer);
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
		networkHandlerUDP = new FileServiceUDPNetworkHandler(datagramChannel, this);
		addHandler(networkHandlerUDP);
		certificationProtocolHandler = new ConnectedCertificationProtocolHandler(networkHandlerUDP, storer);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(localAddress);
		networkHandlerTCP = new FileServiceTCPNetworkHandler(serverSocketChannel, this);
		addHandler(networkHandlerTCP);
		fileServiceProtocolHandler = new ConnectedFileServiceProtocolHandler(this, storer, fileServiceProvider, networkHandlerTCP);
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

	@Override
	public int write(SocketAddress address, SerializerBuffer buffer) throws IOException {
		return networkHandlerTCP.write(address, buffer);
	}
	
	public static void main(String[] args) throws NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeySpecException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		SimpleFileService service = new SimpleFileService("Service-1", "store_service1", new InetSocketAddress(8890), new InetSocketAddress(8888));
		service.start();
		service.makeCertificationRequest();
	}
}
