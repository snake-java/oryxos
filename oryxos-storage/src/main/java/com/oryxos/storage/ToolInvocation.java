package com.oryxos.storage;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * ToolInvocation entity - records each tool call for audit purposes.
 */
@Entity
@Table(name = "tool_invocations", indexes = {
    @Index(name = "idx_tool_invocations_session_id", columnList = "session_id")
})
public class ToolInvocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, length = 255)
    private String sessionId;

    @Column(name = "tool_name", nullable = false, length = 100)
    private String toolName;

    @Column(name = "input_json", nullable = false, columnDefinition = "TEXT")
    private String inputJson;

    @Column(name = "result_json", columnDefinition = "TEXT")
    private String resultJson;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ToolInvocation() {}

    public ToolInvocation(String sessionId, String toolName, String inputJson) {
        this.sessionId = sessionId;
        this.toolName = toolName;
        this.inputJson = inputJson;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getToolName() { return toolName; }
    public void setToolName(String toolName) { this.toolName = toolName; }

    public String getInputJson() { return inputJson; }
    public void setInputJson(String inputJson) { this.inputJson = inputJson; }

    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public void markSuccess(String resultJson, long durationMs) {
        this.success = true;
        this.resultJson = resultJson;
        this.durationMs = durationMs;
    }

    public void markFailure(String errorMessage, long durationMs) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.durationMs = durationMs;
    }
}
