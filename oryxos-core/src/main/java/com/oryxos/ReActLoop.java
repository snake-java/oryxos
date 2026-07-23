package com.oryxos;

import java.util.List;

/**
 * ReAct Loop - Agent's core reasoning engine
 *
 * Algorithm:
 * 1. Append user message to session history
 * 2. Assemble prompt (system + memory + history + tools)
 * 3. Call LLM Provider
 * 4. If no tool call -> return final response
 * 5. If tool call -> execute tool, append result to history -> back to step 2
 * 6. Max iterations reached -> force end
 */
public class ReActLoop {

    private final ProviderService providerService;
    private final ToolExecutor toolExecutor;
    private final PromptBuilder promptBuilder;
    private final int maxIterations;

    public ReActLoop(
            ProviderService providerService,
            ToolExecutor toolExecutor,
            PromptBuilder promptBuilder,
            int maxIterations) {
        this.providerService = providerService;
        this.toolExecutor = toolExecutor;
        this.promptBuilder = promptBuilder;
        this.maxIterations = maxIterations;
    }

    public String run(Session session, String userMessage) {
        session.addMessage(Message.user(userMessage));

        for (int i = 0; i < maxIterations; i++) {
            // Assemble prompt
            String prompt = promptBuilder.build(session);

            // Call LLM
            String response = providerService.call(session.getProfile(), prompt);

            // Parse response
            if (response == null || response.isBlank()) {
                return "No response from LLM";
            }

            // Check if it's a tool call (simplified - real implementation needs proper parsing)
            ToolCall toolCall = parseToolCall(response);

            if (toolCall == null) {
                // No tool call - return response
                session.addMessage(Message.assistant(response));
                return response;
            }

            // Execute tool
            OryxTool.ToolResult result = toolExecutor.execute(session.getProfile(), toolCall.name(), toolCall.arguments());

            // Append tool result to history
            session.addMessage(Message.tool(toolCall.name(), toolCall.arguments(), result.content()));

            // Check if tool execution failed
            if (!result.success()) {
                return "Tool execution failed: " + result.errorMessage();
            }
        }

        return "Max iterations reached";
    }

    /**
     * Parse tool call from LLM response.
     * Real implementation needs to parse Function Calling format.
     */
    private ToolCall parseToolCall(String response) {
        // Simplified parsing - needs proper implementation
        // This should parse Function Calling format from LLM
        return null;
    }

    public record ToolCall(String name, String arguments) {}
}
