package service;

import java.net.SocketAddress;

import protocol.AbstractProtocolHandler;
import protocol.NetworkWriter;
import protocol.message.certification.AuthReply;
import protocol.message.certification.AuthRequest;
import protocol.message.certification.CertReply;
import protocol.message.certification.CertRequest;

public class SimpleServiceProtocolHandler extends AbstractProtocolHandler implements ServiceProtocolHandler {

	public SimpleServiceProtocolHandler(NetworkWriter networkWriter) {
		super(networkWriter);
	}

	@Override
	public void sendAuthRequest(SocketAddress to, AuthRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendCertRequest(SocketAddress to, CertRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleAuthReply(SocketAddress from, AuthReply reply) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCertReply(SocketAddress from, CertReply reply) {
		// TODO Auto-generated method stub
		
	}

}
