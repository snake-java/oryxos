package com.oryxos.tool;

import com.oryxos.OryxTool;

import java.util.Map;

/**
 * McpToolAdapter - wraps MCP tool calls as OryxTool instances.
 */
public class McpToolAdapter implements OryxTool {

    private final String name;
    private final String description;
    private final String inputSchema;
    private final McpClientService mcpClient;
    private final String serverName;

    public McpToolAdapter(String name, String description, String inputSchema,
                          McpClientService mcpClient, String serverName) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
        this.mcpClient = mcpClient;
        this.serverName = serverName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getInputSchema() {
        return inputSchema;
    }

    @Override
    public ToolResult execute(String input) {
        // Parse input and call MCP server
        Map<String, Object> args = parseInput(input);
        McpClientService.McpResponse response = mcpClient.callTool(serverName, name, args);

        if (response.success()) {
            return new ToolResult(true, response.result(), null, false);
        } else {
            return new ToolResult(false, null, response.error(), true);
        }
    }

    private Map<String, Object> parseInput(String input) {
        // Simplified JSON parsing - in production use proper JSON library
        return Map.of();
    }
}
