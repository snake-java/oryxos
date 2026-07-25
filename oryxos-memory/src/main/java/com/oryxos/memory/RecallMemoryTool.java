package com.oryxos.memory;

import com.oryxos.OryxTool;

import java.util.List;

/**
 * Built-in Tool: recall_memory
 * Searches long-term memory by keyword or regex.
 */
public class RecallMemoryTool implements OryxTool {

    private final MemoryService memoryService;

    public RecallMemoryTool(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @Override
    public String getName() {
        return "recall_memory";
    }

    @Override
    public String getDescription() {
        return "Search long-term memory for information. Supports keyword search (case-insensitive) and regex patterns.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "query": {
                  "type": "string",
                  "description": "Keyword or regex pattern to search for"
                },
                "mode": {
                  "type": "string",
                  "enum": ["keyword", "regex"],
                  "description": "Search mode: 'keyword' for case-insensitive contains, 'regex' for pattern matching"
                }
              },
              "required": ["query"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String query = extractQuery(input);
        String mode = extractMode(input);

        if (query == null) {
            return new ToolResult(false, null, "Invalid input: missing query", false);
        }

        try {
            List<String> results;
            if ("regex".equalsIgnoreCase(mode)) {
                results = memoryService.recallMemoryRegex(query);
            } else {
                results = memoryService.recallMemory(query);
            }

            if (results.isEmpty()) {
                return new ToolResult(true, "No matching memories found.", null, false);
            }

            return new ToolResult(true, String.join("\n", results), null, false);
        } catch (Exception e) {
            return new ToolResult(false, null, "Failed to recall memory: " + e.getMessage(), false);
        }
    }

    private String extractQuery(String input) {
        int queryIndex = input.indexOf("\"query\"");
        if (queryIndex < 0) return null;
        int colon = input.indexOf(':', queryIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }

    private String extractMode(String input) {
        int modeIndex = input.indexOf("\"mode\"");
        if (modeIndex < 0) return "keyword";
        int colon = input.indexOf(':', modeIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }
}
