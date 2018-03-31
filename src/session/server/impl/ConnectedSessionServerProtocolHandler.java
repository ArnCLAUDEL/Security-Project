package session.server.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;

import certification.ICertificationStorer;
import protocol.NetworkWriter;
import protocol.message.session.SessionReply;
import protocol.message.session.SessionRequest;
import session.server.ISessionProvider;
import util.Cheat;
import util.KeyGenerator;
import util.SerializerBuffer;

public class ConnectedSessionServerProtocolHandler extends ASessionServerProtocolHandler {

	public ConnectedSessionServerProtocolHandler(NetworkWriter networkWriter, ISessionProvider provider, ICertificationStorer storer) {
		super(networkWriter, provider, storer);
	}
	
	@Override
	public void handleSessionRequest(SocketAddress from, SessionRequest request) {
		try {
			X509CertificateHolder senderCertificate = storer.getCertificate(request.getSenderAlias());
			X509CertificateHolder destinationCertificate = storer.getCertificate(request.getDestinationAlias());
			SecretKey sKey = provider.generateSessionKey();
			
			SerializerBuffer serializerClear = new SerializerBuffer(2048);
			SerializerBuffer serializerEncrypted = new SerializerBuffer(2048);
			
			Cipher rsaCipherDestination = Cipher.getInstance("RSA");
			BCRSAPublicKey publicKeyDestination = KeyGenerator.bcrsaPublicKeyConverter(destinationCertificate);
			rsaCipherDestination.init(Cipher.PUBLIC_KEY, publicKeyDestination);
			serializerClear.putString(request.getSenderAlias());
			serializerClear.putByteArray(sKey.getEncoded());
			serializerClear.flip();
			rsaCipherDestination.doFinal(serializerClear.getBuffer(), serializerEncrypted.getBuffer());
			serializerEncrypted.flip();
			int length = serializerEncrypted.remaining();
			byte[] encodedMessage = new byte[length];
			serializerEncrypted.get(encodedMessage);
			
			Cipher rsaCipherSender = Cipher.getInstance("RSA");
			BCRSAPublicKey publicKeySender = KeyGenerator.bcrsaPublicKeyConverter(senderCertificate);
			rsaCipherSender.init(Cipher.PUBLIC_KEY, publicKeySender);
			
			sendSessionreply(from, new SessionReply(request.getId(), request.getSenderNonce(), request.getDestinationAlias(), encodedMessage, sKey), rsaCipherSender);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | CertificateEncodingException | KeyStoreException | IOException | InvalidKeyException | InvalidKeySpecException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
			Cheat.LOGGER.log(Level.SEVERE, "Error while handling SessionRequest.", e);
		}
	}

	@Override
	public void sendSessionreply(SocketAddress to, SessionReply reply, Cipher rsaCipher) {
		send(to, reply, rsaCipher);
	}


}
