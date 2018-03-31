package session.client;

public interface ISessionManager {
	SessionInfo getSessionInfo(long id);
	boolean createSession(long id, SessionInfo info);	
}
