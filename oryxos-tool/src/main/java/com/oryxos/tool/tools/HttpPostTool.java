package com.oryxos.tool.tools;

import com.oryxos.OryxTool;
import com.oryxos.Sandbox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Built-in Tool: http_post
 * Performs HTTP POST requests to whitelisted domains.
 */
public class HttpPostTool implements OryxTool {

    private final Sandbox sandbox;
    private final HttpClient httpClient;

    public HttpPostTool(Sandbox sandbox) {
        this.sandbox = sandbox;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String getName() {
        return "http_post";
    }

    @Override
    public String getDescription() {
        return "Perform an HTTP POST request to a URL with optional body.";
    }

    @Override
    public String getInputSchema() {
        return """
            {
              "type": "object",
              "properties": {
                "url": {
                  "type": "string",
                  "description": "URL to post to"
                },
                "body": {
                  "type": "string",
                  "description": "Request body (optional)"
                },
                "contentType": {
                  "type": "string",
                  "description": "Content type (default: application/json)"
                }
              },
              "required": ["url"]
            }
            """;
    }

    @Override
    public ToolResult execute(String input) {
        String url = extractUrl(input);
        String body = extractBody(input);
        String contentType = extractContentType(input);

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
                    .POST(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(30));

            if (body != null) {
                requestBuilder.header("Content-Type", contentType);
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return new ToolResult(true, response.body(), null, false);

        } catch (Exception e) {
            return new ToolResult(false, null, "HTTP POST failed: " + e.getMessage(), true);
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

    private String extractBody(String input) {
        int bodyIndex = input.indexOf("\"body\"");
        if (bodyIndex < 0) return null;
        int colon = input.indexOf(':', bodyIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }

    private String extractContentType(String input) {
        int ctIndex = input.indexOf("\"contentType\"");
        if (ctIndex < 0) return "application/json";
        int colon = input.indexOf(':', ctIndex);
        int startQuote = input.indexOf('"', colon);
        int endQuote = input.indexOf('"', startQuote + 1);
        return input.substring(startQuote + 1, endQuote);
    }
}
