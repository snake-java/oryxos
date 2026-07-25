package com.oryxos.tool;

import com.oryxos.OryxTool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ToolRegistry - central registry for all tools.
 * Tools can be built-in, MCP, or @Tool annotated.
 */
public class ToolRegistry {

    private final Map<String, OryxTool> tools = new ConcurrentHashMap<>();

    /**
     * Register a tool.
     * @param tool OryxTool instance
     */
    public void register(OryxTool tool) {
        tools.put(tool.getName(), tool);
    }

    /**
     * Get a tool by name.
     * @param name tool name
     * @return OryxTool or null if not found
     */
    public OryxTool get(String name) {
        return tools.get(name);
    }

    /**
     * Check if a tool is registered.
     * @param name tool name
     * @return true if registered
     */
    public boolean contains(String name) {
        return tools.containsKey(name);
    }

    /**
     * Get all registered tool names.
     * @return array of tool names
     */
    public String[] getRegisteredTools() {
        return tools.keySet().toArray(new String[0]);
    }

    /**
     * Get tool description for LLM function calling.
     * @param name tool name
     * @return JSON schema for the tool
     */
    public String getToolSchema(String name) {
        OryxTool tool = tools.get(name);
        if (tool == null) {
            return null;
        }
        return String.format("""
            {
              "name": "%s",
              "description": "%s",
              "parameters": %s
            }
            """,
            tool.getName(),
            tool.getDescription(),
            tool.getInputSchema());
    }
}
