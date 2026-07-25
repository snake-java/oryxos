package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Built-in Tool: http_get
 * Performs HTTP GET requests to whitelisted domains.
 */
public class HttpGetTool implements OryxTool {

    private final Sandbox sandbox;
    private final HttpClient httpClient;

    public HttpGetTool(Sandbox sandbox) {
        this.sandbox = sandbox;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String getName() {
        return "http_get";
    }

    @Override
    public String getDescription() {
        return "Perform an HTTP GET request to a URL.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "url": {
                  "type": "string",
                  "description": "URL to fetch"
                },
                "headers": {
                  "type": "object",
                  "description": "Optional HTTP headers"
                }
              },
              "required": ["url"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String url = extractUrl(input);
        if (url == null) {
            return new ToolResult(false, null, "Invalid input: missing url", false);
        }

        try {
            sandbox.enforce(getName(), input);
        } catch (Exception e) {
            return new ToolResult(false, null, e.getMessage(), false);
        }

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(30));

            // Add headers if present (simplified)
            HttpRequest request = requestBuilder.build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return new ToolResult(true, response.body(), null, false);

        } catch (Exception e) {
            return new ToolResult(false, null, "HTTP GET failed: " + e.getMessage(), true);
        }
    }

    private String extractUrl(String input) {
        int urlIndex = input.indexOf("\"url\"");
        if (urlIndex < 0) return null;
        int colon = input.indexOf(':', urlIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }
}
