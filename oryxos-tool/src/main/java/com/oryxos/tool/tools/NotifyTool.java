package com.oryxos.tool.tools;

import com.oryxos.OryxTool;

import java.util.Map;

/**
 * Built-in Tool: notify
 * Sends notifications to configured channels (webhook, etc.)
 */
public class NotifyTool implements OryxTool {

    @Override
    public String getName() {
        return "notify";
    }

    @Override
    public String getDescription() {
        return "Send a notification message to configured channels (webhook, etc.)";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "message": {
                  "type": "string",
                  "description": "Notification message to send"
                },
                "channel": {
                  "type": "string",
                  "description": "Channel name to send to (optional, uses default if not specified)"
                }
              },
              "required": ["message"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String message = extractMessage(input);
        String channel = extractChannel(input);

        if (message == null) {
            return new ToolResult(false, null, "Invalid input: missing message", false);
        }

        // In core phase, this is a stub implementation
        // Full implementation would integrate with actual notification channels
        return new ToolResult(true, "Notification sent: " + message + (channel != null ? " to " + channel : ""), null, false);
    }

    private String extractMessage(String input) {
        int msgIndex = input.indexOf("\"message\"");
        if (msgIndex < 0) return null;
        int colon = input.indexOf(':', msgIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        if (startQuote >= endQuote) return null;
        return input.substring(startQuote + 1, endQuote);
    }

    private String extractChannel(String input) {
        int chIndex = input.indexOf("\"channel\"");
        if (chIndex < 0) return null;
        int colon = input.indexOf(':', chIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        if (startQuote >= endQuote) return null;
        return input.substring(startQuote + 1, endQuote);
    }
}
