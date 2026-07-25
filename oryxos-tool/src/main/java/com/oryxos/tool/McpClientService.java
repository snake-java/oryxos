package com.oryxos.tool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * McpClientService - MCP (Model Context Protocol) client for external tool servers.
 * Handles JSON-RPC 2.0 communication with MCP servers.
 */
public class McpClientService {

    private final HttpClient httpClient;
    private final Map<String, McpServerConfig> servers;

    public McpClientService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.servers = new ConcurrentHashMap<>();
    }

    /**
     * Register an MCP server.
     */
    public void registerServer(String name, String url) {
        servers.put(name, new McpServerConfig(name, url));
    }

    /**
     * Call an MCP server tool.
     */
    public McpResponse callTool(String serverName, String toolName, Map<String, Object> arguments) {
        McpServerConfig config = servers.get(serverName);
        if (config == null) {
            return new McpResponse(false, null, "Server not found: " + serverName);
        }

        // Build JSON-RPC 2.0 request
        String requestBody = String.format("""
            {
              "jsonrpc": "2.0",
              "method": "tools/call",
              "params": {
                "name": "%s",
                "arguments": %s
              },
              "id": 1
            }
            """, toolName, toJson(arguments));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.url()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new McpResponse(true, response.body(), null);
            } else {
                return new McpResponse(false, null, "HTTP " + response.statusCode() + ": " + response.body());
            }

        } catch (Exception e) {
            return new McpResponse(false, null, "MCP call failed: " + e.getMessage());
        }
    }

    private String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public record McpServerConfig(String name, String url) {}
    public record McpResponse(boolean success, String result, String error) {}
}
