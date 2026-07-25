package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Built-in Tool: list_dir
 * Lists directory contents at whitelisted paths.
 */
public class ListDirTool implements OryxTool {

    private final Sandbox sandbox;

    public ListDirTool(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String getName() {
        return "list_dir";
    }

    @Override
    public String getDescription() {
        return "List the contents of a directory.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "dir": {
                  "type": "string",
                  "description": "Path to the directory to list"
                }
              },
              "required": ["dir"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String dir = extractDir(input);
        if (dir == null) {
            return new ToolResult(false, null, "Invalid input: missing dir", false);
        }

        try {
            sandbox.enforce(getName(), input);
        } catch (Exception e) {
            return new ToolResult(false, null, e.getMessage(), false);
        }

        try {
            Path dirPath = Path.of(dir);
            StringBuilder result = new StringBuilder();
            try (var entries = Files.list(dirPath)) {
                entries.forEach(entry -> {
                    String type = Files.isDirectory(entry) ? "[DIR]" : "[FILE]";
                    result.append(type).append(" ").append(entry.getFileName()).append("\n");
                });
            }
            return new ToolResult(true, result.toString(), null, false);
        } catch (IOException e) {
            return new ToolResult(false, null, "Failed to list directory: " + e.getMessage(), false);
        }
    }

    private String extractDir(String input) {
        int dirIndex = input.indexOf("\"dir\"");
        if (dirIndex < 0) return null;
        int colon = input.indexOf(':', dirIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }
}
