package com.oryxos;

/**
 * Core Tool abstraction interface.
 * All tools (built-in, MCP, @Tool) are wrapped as OryxTool instances.
 */
public interface OryxTool {

    /**
     * Tool name
     */
    String getName();

    /**
     * Tool description for LLM
     */
    String getDescription();

    /**
     * JSON Schema for tool input
     */
    String getInputSchema();

    /**
     * Execute the tool with given input
     * @param input JSON input
     * @return Tool result
     */
    ToolResult execute(String input);

    /**
     * Tool execution result
     */
    record ToolResult(
        boolean success,
        String content,
        String errorMessage,
        boolean retryable
    ) {}
}
