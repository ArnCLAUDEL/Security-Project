package client;

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
import client.state.ConnectedClientProtocolHandler;
import client.state.NotConnectedClientProtocolHandler;
import io.AbstractKeyboardHandler;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;
import protocol.message.service.file.ServiceFileReadReply;
import protocol.message.service.file.ServiceFileReadRequest;
import protocol.message.service.file.ServiceFileWriteReply;
import protocol.message.service.file.ServiceFileWriteRequest;
import util.Cheat;
import util.SerializerBuffer;

public class SimpleClient extends AbstractCertificatedEntity implements Client {
	private final SocketAddress fileServiceAddress;
	
	private ClientProtocolHandler clientProtocolHandler;
	private CertificationProtocolHandler certificationProtocolHandler;
	private ClientUDPNetworkHandler networkHandlerUDP;
	private ClientTCPNetworkHandler networkHandlerTCP;
	
	public SimpleClient(String name, String keyStoreAlias, SocketAddress localAddress, SocketAddress caAddress, SocketAddress fileServiceAddress) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, NoSuchProviderException {
		super(name, keyStoreAlias, localAddress, caAddress);
		this.clientProtocolHandler = new NotConnectedClientProtocolHandler();
		this.certificationProtocolHandler = new NotConnectedCertificationProtocolHandler(storer);
		this.fileServiceAddress = fileServiceAddress;
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
		sendServiceFileWrite(fileServiceAddress, new ServiceFileWriteRequest(filename, content));
	}
	
	@Override
	public void readFrom(String filename) {
		sendServiceFileRead(fileServiceAddress, new ServiceFileReadRequest(filename));
	}
	
	@Override
	public void sendServiceFileRead(SocketAddress to, ServiceFileReadRequest request) {
		clientProtocolHandler.sendServiceFileRead(to, request);
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
		networkHandlerUDP = new ClientUDPNetworkHandler(datagramChannel, this);
		addHandler(networkHandlerUDP);
		certificationProtocolHandler = new ConnectedCertificationProtocolHandler(networkHandlerUDP, storer);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(localAddress);
		networkHandlerTCP = new ClientTCPNetworkHandler(serverSocketChannel, this);
		addHandler(networkHandlerTCP);
		clientProtocolHandler = new ConnectedClientProtocolHandler(networkHandlerTCP);
		final Client client = this;
		networkHandlerTCP.connect(fileServiceAddress);
		
		addHandler(new AbstractKeyboardHandler() {
			
			@Override
			protected void handle(SerializerBuffer serializerBuffer) throws IOException {
				Cheat.LOGGER.log(Level.INFO, "Preparing writing..");
				client.writeTo("test.txt", "Hello World");
				Cheat.LOGGER.log(Level.INFO, "Writing sent..");
				try {Thread.sleep(1000);}
				catch (InterruptedException e) {}
				Cheat.LOGGER.log(Level.INFO, "");
				Cheat.LOGGER.log(Level.INFO, "Preparing reading..");
				client.readFrom("test.txt");
				Cheat.LOGGER.log(Level.INFO, "Reading sent..");
			}
		});
	}
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException, InvalidKeySpecException {
		Cheat.setLoggerLevelDisplay(Level.ALL);
		Client client = new SimpleClient("arnaud", "store_client", new InetSocketAddress(8889), new InetSocketAddress(8888), new InetSocketAddress(8890));
		new Thread(client).start();
		client.makeCertificationRequest();
	}
	
}
