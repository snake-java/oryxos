package com.oryxos;

import java.util.List;
import java.util.Optional;

/**
 * Session Manager - manages session lifecycle
 */
public interface SessionManager {

    /**
     * Get session by ID
     */
    Optional<Session> getSession(String sessionId);

    /**
     * Save session
     */
    void saveSession(Session session);

    /**
     * List sessions by profile
     */
    List<Session> listSessions(String profileName);

    /**
     * Archive session
     */
    void archiveSession(String sessionId);

    /**
     * Delete session
     */
    void deleteSession(String sessionId);
}
