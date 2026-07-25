package com.oryxos.storage;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Session entity - stores conversation context between user and Agent.
 * Session ID is generated from channel + user_id + profile_name.
 */
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "profile_name", nullable = false, length = 255)
    private String profileName;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "messages_json", nullable = false, columnDefinition = "TEXT")
    private String messagesJson;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // active / archived

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    public Session() {}

    public Session(String sessionId, String profileName, String channel, String userId) {
        this.sessionId = sessionId;
        this.profileName = profileName;
        this.channel = channel;
        this.userId = userId;
        this.status = "active";
        this.messagesJson = "[]";
        this.createdAt = Instant.now();
        this.lastActiveAt = Instant.now();
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessagesJson() { return messagesJson; }
    public void setMessagesJson(String messagesJson) { this.messagesJson = messagesJson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(Instant lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public Instant getArchivedAt() { return archivedAt; }
    public void setArchivedAt(Instant archivedAt) { this.archivedAt = archivedAt; }

    public void archive() {
        this.status = "archived";
        this.archivedAt = Instant.now();
    }

    public void touch() {
        this.lastActiveAt = Instant.now();
    }
}
