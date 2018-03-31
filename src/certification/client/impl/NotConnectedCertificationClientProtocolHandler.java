package certification.client.impl;

import certification.ICertificationStorer;

public class NotConnectedCertificationClientProtocolHandler extends ACertificationClientProtocolHandler {

	public NotConnectedCertificationClientProtocolHandler(ICertificationStorer storer) {
		super(null, storer);
	}

}
