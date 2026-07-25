package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Built-in Tool: write_file
 * Writes content to a file at whitelisted paths.
 */
public class WriteFileTool implements OryxTool {

    private final Sandbox sandbox;

    public WriteFileTool(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String getName() {
        return "write_file";
    }

    @Override
    public String getDescription() {
        return "Write content to a file. Creates the file if it doesn't exist, overwrites if it does.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "path": {
                  "type": "string",
                  "description": "Path to the file to write"
                },
                "content": {
                  "type": "string",
                  "description": "Content to write to the file"
                }
              },
              "required": ["path", "content"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String path = extractPath(input);
        String content = extractContent(input);
        if (path == null || content == null) {
            return new ToolResult(false, null, "Invalid input: missing path or content", false);
        }

        try {
            sandbox.enforce(getName(), input);
        } catch (Exception e) {
            return new ToolResult(false, null, e.getMessage(), false);
        }

        try {
            Path filePath = Path.of(path);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
            return new ToolResult(true, "File written successfully: " + path, null, false);
        } catch (IOException e) {
            return new ToolResult(false, null, "Failed to write file: " + e.getMessage(), false);
        }
    }

    private String extractPath(String input) {
        int pathIndex = input.indexOf("\"path\"");
        if (pathIndex < 0) return null;
        int colon = input.indexOf(':', pathIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }

    private String extractContent(String input) {
        int contentIndex = input.indexOf("\"content\"");
        if (contentIndex < 0) return null;
        int colon = input.indexOf(':', contentIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.lastIndexOf('"');
        if (startQuote >= endQuote) return null;
        return input.substring(startQuote + 1, endQuote);
    }
}
