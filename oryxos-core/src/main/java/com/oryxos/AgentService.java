package com.oryxos;

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
        Session session = sessionManager.getSession(sessionId);
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

    private String generateSessionId(String channel, String userId, String profileName) {
        return channel + ":" + userId + ":" + profileName;
    }
}
