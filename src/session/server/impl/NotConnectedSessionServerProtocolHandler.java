package session.server.impl;

import certification.ICertificationStorer;
import session.server.ISessionProvider;

public class NotConnectedSessionServerProtocolHandler extends ASessionServerProtocolHandler {

	public NotConnectedSessionServerProtocolHandler(ISessionProvider provider, ICertificationStorer storer) {
		super(null, provider, storer);
	}

}
