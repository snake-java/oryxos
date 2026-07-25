package com.oryxos.storage;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * TaskExecution entity - records each scheduled task execution.
 */
@Entity
@Table(name = "task_executions")
public class TaskExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false, length = 255)
    private String taskId;

    @Column(name = "session_id", nullable = false, length = 255)
    private String sessionId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    public TaskExecution() {}

    public TaskExecution(String taskId, String sessionId) {
        this.taskId = taskId;
        this.sessionId = sessionId;
        this.startedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public void markSuccess(long durationMs) {
        this.success = true;
        this.durationMs = durationMs;
    }

    public void markFailure(String errorMessage, long durationMs) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
    }
}
