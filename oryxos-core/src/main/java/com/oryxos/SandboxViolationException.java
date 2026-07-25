package com.oryxos;

/**
 * Exception thrown when tool execution violates sandbox constraints.
 */
public class SandboxViolationException extends RuntimeException {

    private final String toolName;
    private final String reason;

    public SandboxViolationException(String toolName, String reason) {
        super("Sandbox violation for tool '" + toolName + "': " + reason);
        this.toolName = toolName;
        this.reason = reason;
    }

    public String getToolName() {
        return toolName;
    }

    public String getReason() {
        return reason;
    }
}
