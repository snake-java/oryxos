package com.oryxos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Prompt Builder - assembles prompt for each LLM call
 *
 *组装顺序:
 * 1. System prompt (AGENT.md + Bootstrap files)
 * 2. Memory injection (session + long-term)
 * 3. Conversation history (truncated by maxHistoryTurns)
 * 4. Available tools list (Function Calling format)
 */
public class PromptBuilder {

    private final ContextLoader contextLoader;
    private final MemoryService memoryService;

    public PromptBuilder(ContextLoader contextLoader, MemoryService memoryService) {
        this.contextLoader = contextLoader;
        this.memoryService = memoryService;
    }

    public String build(Session session) {
        StringBuilder prompt = new StringBuilder();

        // 1. System prompt
        prompt.append("# System Prompt\n\n");
        prompt.append(contextLoader.loadSystemPrompt(session.getProfile()));
        prompt.append("\n\n");
        prompt.append("Current date and time: ");
        prompt.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        prompt.append("\n\n");

        // 2. Memory injection
        prompt.append("# Memory\n\n");
        prompt.append(memoryService.getContextForPrompt(session));
        prompt.append("\n\n");

        // 3. Conversation history
        prompt.append("# Conversation History\n\n");
        int maxHistory = session.getProfile().settings().maxHistoryTurns();
        var messages = session.getMessages();
        int start = Math.max(0, messages.size() - maxHistory);
        for (int i = start; i < messages.size(); i++) {
            Message msg = messages.get(i);
            prompt.append(formatMessage(msg));
        }
        prompt.append("\n\n");

        // 4. Tools list
        prompt.append("# Available Tools\n\n");
        prompt.append("Use tools when needed to answer the user's question.\n");

        return prompt.toString();
    }

    private String formatMessage(Message msg) {
        return switch (msg.getRole()) {
            case USER -> "User: " + msg.getContent() + "\n\n";
            case ASSISTANT -> "Assistant: " + msg.getContent() + "\n\n";
            case SYSTEM -> "System: " + msg.getContent() + "\n\n";
            case TOOL -> "Tool " + msg.getToolName() + "(" + msg.getToolInput() + ") -> " + msg.getToolResult() + "\n\n";
        };
    }
}
