package com.oryxos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tool Executor - executes tools registered in ToolRegistry
 */
public class ToolExecutor {

    private final Map<String, OryxTool> toolRegistry;
    private final Sandbox sandbox;

    public ToolExecutor(Sandbox sandbox) {
        this.toolRegistry = new ConcurrentHashMap<>();
        this.sandbox = sandbox;
    }

    public void registerTool(OryxTool tool) {
        toolRegistry.put(tool.getName(), tool);
    }

    public OryxTool.ToolResult execute(Profile profile, String toolName, String arguments) {
        OryxTool tool = toolRegistry.get(toolName);

        if (tool == null) {
            return new OryxTool.ToolResult(false, null, "Tool not found: " + toolName, false);
        }

        // Check if profile allows this tool
        if (profile.tools() != null && !profile.tools().isEmpty()
                && !profile.tools().contains(toolName)) {
            return new OryxTool.ToolResult(false, null, "Tool not allowed by profile: " + toolName, false);
        }

        // Sandbox check
        if (!sandbox.check(toolName, arguments)) {
            return new OryxTool.ToolResult(false, null, "Sandbox violation", false);
        }

        try {
            return tool.execute(arguments);
        } catch (Exception e) {
            return new OryxTool.ToolResult(false, null, e.getMessage(), true);
        }
    }

    public OryxTool getTool(String name) {
        return toolRegistry.get(name);
    }

    public Map<String, OryxTool> getAllTools() {
        return Map.copyOf(toolRegistry);
    }
}
