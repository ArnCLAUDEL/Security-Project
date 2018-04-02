package session.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import session.client.ISessionManager;
import session.client.SessionIdentifier;
import session.client.SessionInfo;
import util.Cheat;

public class SessionManager implements ISessionManager {

	private final Map<Long, SessionInfo> idSessions;
	
	public SessionManager() {
		this.idSessions = new HashMap<>();
	}
	
	@Override
	public SessionInfo getSessionInfo(long id) throws NoSuchElementException {
		if(!idSessions.containsKey(id))
			throw new NoSuchElementException("Session " + id + " not found.");
		return idSessions.get(id);
	}

	@Override
	public boolean createSession(long id, SessionInfo info) {
		if(idSessions.containsKey(id))
			return false;
		
		Cheat.LOGGER.log(Level.INFO, "Session " + id + " created.");
		idSessions.put(id, info);
		return true;
	}
	
	@Override
	public void deleteSession(long id) {
		idSessions.remove(id);
		Cheat.LOGGER.log(Level.INFO, "Session " + id + " deleted.");
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
