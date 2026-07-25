package com.oryxos;

/**
 * Sandbox - security boundary for tool execution
 */
public interface Sandbox {

    /**
     * Check if tool execution is allowed
     * @param toolName tool name
     * @param arguments tool arguments (JSON)
     * @return true if allowed
     */
    boolean check(String toolName, String arguments);

    /**
     * Enforce sandbox - throws SandboxViolationException if not allowed
     */
    default void enforce(String toolName, String arguments) {
        if (!check(toolName, arguments)) {
            throw new SandboxViolationException(toolName, "Sandbox check failed");
        }
    }
}
