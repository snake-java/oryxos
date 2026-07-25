package com.oryxos.memory;

import com.oryxos.OryxTool;

/**
 * Built-in Tool: save_memory
 * Saves content to long-term memory.
 */
public class SaveMemoryTool implements OryxTool {

    private final MemoryService memoryService;

    public SaveMemoryTool(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @Override
    public String getName() {
        return "save_memory";
    }

    @Override
    public String getDescription() {
        return "Save important information to long-term memory. Use this to remember user preferences, key decisions, or context that should persist across conversations.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "content": {
                  "type": "string",
                  "description": "Content to save to memory"
                }
              },
              "required": ["content"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String content = extractContent(input);
        if (content == null) {
            return new ToolResult(false, null, "Invalid input: missing content", false);
        }

        try {
            memoryService.saveMemory(content);
            return new ToolResult(true, "Memory saved successfully", null, false);
        } catch (Exception e) {
            return new ToolResult(false, null, "Failed to save memory: " + e.getMessage(), false);
        }
    }

    private String extractContent(String input) {
        int contentIndex = input.indexOf("\"content\"");
        if (contentIndex < 0) return null;
        int colon = input.indexOf(':', contentIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        if (startQuote >= endQuote) return null;
        return input.substring(startQuote + 1, endQuote);
    }
}
