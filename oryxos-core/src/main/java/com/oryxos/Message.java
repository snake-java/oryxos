package com.oryxos;

import java.time.Instant;

/**
 * Message - conversation message
 */
public class Message {

    private final Role role;
    private final String content;
    private final Instant timestamp;
    private final String toolName;
    private final String toolInput;
    private final String toolResult;

    public Message(Role role, String content) {
        this(role, content, null, null, null);
    }

    public Message(Role role, String content, String toolName, String toolInput, String toolResult) {
        this.role = role;
        this.content = content;
        this.timestamp = Instant.now();
        this.toolName = toolName;
        this.toolInput = toolInput;
        this.toolResult = toolResult;
    }

    public static Message user(String content) {
        return new Message(Role.USER, content);
    }

    public static Message assistant(String content) {
        return new Message(Role.ASSISTANT, content);
    }

    public static Message tool(String toolName, String toolInput, String toolResult) {
        return new Message(Role.TOOL, null, toolName, toolInput, toolResult);
    }

    public static Message system(String content) {
        return new Message(Role.SYSTEM, content);
    }

    // Getters
    public Role getRole() { return role; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }
    public String getToolName() { return toolName; }
    public String getToolInput() { return toolInput; }
    public String getToolResult() { return toolResult; }

    public boolean isToolCall() {
        return toolName != null;
    }

    public enum Role {
        USER, ASSISTANT, SYSTEM, TOOL
    }
}
