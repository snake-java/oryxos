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
}
