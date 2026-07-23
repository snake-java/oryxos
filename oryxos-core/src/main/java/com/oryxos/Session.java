package com.oryxos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Session - conversation context container
 */
public class Session {

    private final String sessionId;
    private final String profileName;
    private final String channel;
    private final String userId;
    private final List<Message> messages;
    private SessionStatus status;
    private final Instant createdAt;
    private Instant lastActiveAt;
    private Instant archivedAt;

    public Session(String sessionId, String profileName, String channel, String userId) {
        this.sessionId = sessionId;
        this.profileName = profileName;
        this.channel = channel;
        this.userId = userId;
        this.messages = new ArrayList<>();
        this.status = SessionStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.lastActiveAt = Instant.now();
    }

    public void addMessage(Message message) {
        messages.add(message);
        lastActiveAt = Instant.now();
    }

    public void archive() {
        this.status = SessionStatus.ARCHIVED;
        this.archivedAt = Instant.now();
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public String getProfileName() { return profileName; }
    public String getChannel() { return channel; }
    public String getUserId() { return userId; }
    public List<Message> getMessages() { return List.copyOf(messages); }
    public SessionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActiveAt() { return lastActiveAt; }
    public Instant getArchivedAt() { return archivedAt; }

    public enum SessionStatus {
        ACTIVE, ARCHIVED
    }
}
