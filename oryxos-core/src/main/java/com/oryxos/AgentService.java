package com.oryxos;

import java.util.List;

/**
 * Agent Service - unified entry point for all trigger sources
 * (CLI, Web Service, Scheduler)
 */
public class AgentService {

    private final ReActLoop reactLoop;
    private final SessionManager sessionManager;

    public AgentService(ReActLoop reactLoop, SessionManager sessionManager) {
        this.reactLoop = reactLoop;
        this.sessionManager = sessionManager;
    }

    /**
     * Process a message - unified entry point
     */
    public String process(String sessionId, String userMessage) {
        Session session = sessionManager.getSession(sessionId).orElse(null);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return reactLoop.run(session, userMessage);
    }

    /**
     * Create a new session
     */
    public Session createSession(String profileName, String channel, String userId) {
        String sessionId = generateSessionId(channel, userId, profileName);
        Session session = new Session(sessionId, profileName, channel, userId);
        sessionManager.saveSession(session);
        return session;
    }

    /**
     * Get session by ID
     */
    public Session getSession(String sessionId) {
        return sessionManager.getSession(sessionId).orElse(null);
    }

    /**
     * Archive session
     */
    public void archiveSession(String sessionId) {
        Session session = getSession(sessionId);
        if (session != null) {
            session.archive();
            sessionManager.saveSession(session);
        }
    }

    /**
     * Invoke agent without session (stateless)
     */
    public String invokeAgent(String agentName, String userId, String message) {
        Session session = createSession(agentName, "api", userId);
        return process(session.getSessionId(), message);
    }

    /**
     * List profiles (placeholder - returns empty list)
     */
    public List<Profile> listProfiles() {
        return List.of();
    }

    /**
     * Get memory content (placeholder)
     */
    public String getMemoryContent(String query) {
        return "";
    }

    /**
     * Get registered tools (placeholder)
     */
    public String[] getRegisteredTools() {
        return new String[0];
    }

    private String generateSessionId(String channel, String userId, String profileName) {
        return channel + ":" + userId + ":" + profileName;
    }
}
