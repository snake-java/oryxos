package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Built-in Tool: shell
 * Executes shell commands at whitelisted paths.
 */
public class ShellTool implements OryxTool {

    private final Sandbox sandbox;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    public ShellTool(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    @Override
    public String getName() {
        return "shell";
    }

    @Override
    public String getDescription() {
        return "Execute a shell command. Only whitelisted commands are allowed.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "command": {
                  "type": "string",
                  "description": "Shell command to execute"
                },
                "timeout": {
                  "type": "integer",
                  "description": "Timeout in seconds (default: 30)"
                }
              },
              "required": ["command"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String command = extractCommand(input);
        int timeout = extractTimeout(input);

        if (command == null) {
            return new ToolResult(false, null, "Invalid input: missing command", false);
        }

        try {
            sandbox.enforce(getName(), input);
        } catch (Exception e) {
            return new ToolResult(false, null, e.getMessage(), false);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output;
            boolean completed = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                return new ToolResult(false, null, "Command timed out after " + timeout + " seconds", true);
            }

            try (var reader = process.getInputStream()) {
                output = new String(reader.readAllBytes());
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return new ToolResult(false, output, "Command exited with code " + exitCode, false);
            }
            return new ToolResult(true, output, null, false);

        } catch (Exception e) {
            return new ToolResult(false, null, "Command failed: " + e.getMessage(), e instanceof TimeoutException);
        }
    }

    private String extractCommand(String input) {
        int cmdIndex = input.indexOf("\"command\"");
        if (cmdIndex < 0) return null;
        int colon = input.indexOf(':', cmdIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }

    private int extractTimeout(String input) {
        int timeoutIndex = input.indexOf("\"timeout\"");
        if (timeoutIndex < 0) return DEFAULT_TIMEOUT_SECONDS;
        int colon = input.indexOf(':', timeoutIndex);
        int start = colon + 1;
        while (start < input.length() && Character.isWhitespace(input.charAt(start))) start++;
        int end = start;
        while (end < input.length() && Character.isDigit(input.charAt(end))) end++;
        if (end <= start) return DEFAULT_TIMEOUT_SECONDS;
        try {
            return Integer.parseInt(input.substring(start, end));
        } catch (NumberFormatException e) {
            return DEFAULT_TIMEOUT_SECONDS;
        }
    }
}
