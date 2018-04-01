package session.client.impl;

import java.util.HashMap;
import java.util.Map;

import session.client.ISessionManager;
import session.client.SessionIdentifier;
import session.client.SessionInfo;

public class SessionManager implements ISessionManager {

	private final Map<Long, SessionInfo> idSessions;
	
	public SessionManager() {
		this.idSessions = new HashMap<>();
	}
	
	@Override
	public SessionInfo getSessionInfo(long id) {
		return idSessions.get(id);
	}

	@Override
	public boolean createSession(long id, SessionInfo info) {
		if(idSessions.containsKey(id))
			return false;
		idSessions.put(id, info);
		return true;
	}

	@Override
	public boolean checkSessionIdentifier(SessionIdentifier sessionIdentifier) {
		if(!idSessions.containsKey(sessionIdentifier.getId()))
			return false;
		
		SessionInfo info = idSessions.get(sessionIdentifier.getId());
		
		if(!info.isValid())
			return false;
		
		return info.getDestinationNonce().get().equals(sessionIdentifier.getNonce());
	}

}
