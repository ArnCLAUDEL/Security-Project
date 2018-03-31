package session.client.impl;

import java.util.HashMap;
import java.util.Map;

import session.client.ISessionManager;
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

}
