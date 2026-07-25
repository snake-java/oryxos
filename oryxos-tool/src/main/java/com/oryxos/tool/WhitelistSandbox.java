package com.oryxos.tool;

import com.oryxos.OryxTool;
import com.oryxos.SandboxViolationException;

import java.util.List;
import java.util.Set;

/**
 * WhitelistSandbox - application-layer sandbox with whitelist enforcement.
 * Follows Constitution Principle VI: Sandbox interface first, then WhitelistSandbox implementation.
 */
public class WhitelistSandbox implements com.oryxos.Sandbox {

    private final Set<String> allowedPaths;
    private final Set<String> allowedCommands;
    private final Set<String> allowedDomains;

    public WhitelistSandbox(List<String> allowedPaths, List<String> allowedCommands, List<String> allowedDomains) {
        this.allowedPaths = Set.copyOf(allowedPaths);
        this.allowedCommands = Set.copyOf(allowedCommands);
        this.allowedDomains = Set.copyOf(allowedDomains);
    }

    @Override
    public boolean check(String toolName, String arguments) {
        return switch (toolName) {
            case "read_file", "write_file", "list_dir" -> checkFileOperation(toolName, arguments);
            case "shell" -> checkShellCommand(arguments);
            case "http_get", "http_post" -> checkHttpRequest(arguments);
            default -> true; // Unknown tools are allowed (will fail at execution)
        };
    }

    private boolean checkFileOperation(String toolName, String arguments) {
        // Extract path from JSON arguments
        String path = extractPath(arguments);
        if (path == null) {
            return false;
        }
        return allowedPaths.stream().anyMatch(pattern -> matchesPattern(path, pattern));
    }

    private boolean checkShellCommand(String arguments) {
        // Extract command from JSON arguments
        String command = extractCommand(arguments);
        if (command == null) {
            return false;
        }
        return allowedCommands.contains(command.split(" ")[0]);
    }

    private boolean checkHttpRequest(String arguments) {
        // Extract domain from JSON arguments
        String domain = extractDomain(arguments);
        if (domain == null) {
            return false;
        }
        return allowedDomains.stream().anyMatch(pattern -> matchesPattern(domain, pattern));
    }

    private String extractPath(String arguments) {
        // Simple JSON parsing - extract "path" field
        int pathIndex = arguments.indexOf("\"path\"");
        if (pathIndex < 0) {
            pathIndex = arguments.indexOf("\"dir\"");
        }
        if (pathIndex < 0) return null;

        int colon = arguments.indexOf(':', pathIndex);
        int startQuote = arguments.indexOf('"', colon);
        int endQuote = arguments.indexOf('"', startQuote + 1);
        return arguments.substring(startQuote + 1, endQuote);
    }

    private String extractCommand(String arguments) {
        // Extract "command" field
        int cmdIndex = arguments.indexOf("\"command\"");
        if (cmdIndex < 0) return null;

        int colon = arguments.indexOf(':', cmdIndex);
        int startQuote = arguments.indexOf('"', colon);
        int endQuote = arguments.indexOf('"', startQuote + 1);
        return arguments.substring(startQuote + 1, endQuote);
    }

    private String extractDomain(String arguments) {
        // Extract "url" field
        int urlIndex = arguments.indexOf("\"url\"");
        if (urlIndex < 0) return null;

        int colon = arguments.indexOf(':', urlIndex);
        int startQuote = arguments.indexOf('"', colon);
        int endQuote = arguments.indexOf('"', startQuote + 1);
        String url = arguments.substring(startQuote + 1, endQuote);

        // Extract domain from URL
        try {
            int protocolEnd = url.indexOf("://");
            int domainStart = protocolEnd >= 0 ? url.indexOf('/', protocolEnd + 3) : 0;
            int domainEnd = url.indexOf('/', domainStart + 1);
            return domainEnd > 0 ? url.substring(domainStart + 1, domainEnd) : url.substring(domainStart + 1);
        } catch (Exception e) {
            return url;
        }
    }

    private boolean matchesPattern(String value, String pattern) {
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("**", ".*").replace("*", "[^/]*");
            return value.matches(regex);
        }
        return value.equals(pattern);
    }

    /**
     * Check and enforce sandbox - throws exception if violation.
     */
    public void enforce(String toolName, String arguments) {
        if (!check(toolName, arguments)) {
            throw new SandboxViolationException(toolName, "Arguments violate sandbox policy: " + arguments);
        }
    }
}
