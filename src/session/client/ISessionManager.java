package session.client;

import java.util.NoSuchElementException;

/**
 * {@code ISessionManager} interface provides a way to load and store
 * sessions.<br />
 * A session is identified by an id and it will be associated with 
 * a {@link SessionInfo}.
 */
public interface ISessionManager {
	
	/**
	 * Retrieves the {@link SessionInfo} associated to the given session id.
	 * @param id The session id
	 * @return The {@code SessionInfo} associated
	 * @throws NoSuchElementException If the session does not exist
	 */
	SessionInfo getSessionInfo(long id) throws NoSuchElementException;
	
	/**
	 * Creates and stores a new session by associating the given id with
	 * the {@link SessionInfo}.
	 * @param id The session id
	 * @param info The {@code SessionInfo} to associate
	 * @return <tt>true</tt> if the session has been stored
	 */
	boolean createSession(long id, SessionInfo info);
	
	/**
	 * Verifies if the session represented by the given {@link SessionIdentifier}
	 * exists and is valid. 
	 * @param sessionIdentifier The session to verify
	 * @return <tt>true</tt> if the session exists and is valid
	 */
	boolean checkSessionIdentifier(SessionIdentifier sessionIdentifier);
	
}