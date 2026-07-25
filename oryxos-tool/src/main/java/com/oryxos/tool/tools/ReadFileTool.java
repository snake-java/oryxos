package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Built-in Tool: read_file
 * Reads file content from whitelisted paths.
 */
public class ReadFileTool implements OryxTool {

    private final Sandbox sandbox;

    public ReadFileTool(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String getName() {
        return "read_file";
    }

    @Override
    public String getDescription() {
        return "Read the content of a file. Returns the file content as text.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "path": {
                  "type": "string",
                  "description": "Path to the file to read"
                }
              },
              "required": ["path"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String path = extractPath(input);
        if (path == null) {
            return new ToolResult(false, null, "Invalid input: missing path", false);
        }

        try {
            sandbox.enforce(getName(), input);
        } catch (Exception e) {
            return new ToolResult(false, null, e.getMessage(), false);
        }

        try {
            Path filePath = Path.of(path);
            String content = Files.readString(filePath);
            return new ToolResult(true, content, null, false);
        } catch (IOException e) {
            return new ToolResult(false, null, "Failed to read file: " + e.getMessage(), false);
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
}
