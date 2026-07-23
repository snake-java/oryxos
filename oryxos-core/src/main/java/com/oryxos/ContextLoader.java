package com.oryxos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Context Loader - loads bootstrap files and AGENT.md into system prompt
 */
public class ContextLoader {

    private final Path workspaceRoot;

    public ContextLoader(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    /**
     * Load system prompt for a profile:
     * - AGENT.md content
     * - Bootstrap files (AGENTS.md, SOUL.md, USER.md)
     */
    public String loadSystemPrompt(Profile profile) {
        StringBuilder context = new StringBuilder();

        // Load AGENT.md
        Path agentDir = workspaceRoot.resolve("agents").resolve(profile.name());
        Path agentMd = agentDir.resolve("AGENT.md");

        if (Files.exists(agentMd)) {
            try {
                context.append("# Agent Definition\n\n");
                context.append(Files.readString(agentMd));
                context.append("\n\n");
            } catch (IOException e) {
                context.append("# Agent: ").append(profile.identity().agentName()).append("\n\n");
            }
        }

        // Load bootstrap files
        List<String> bootstrapFiles = profile.bootstrap();
        if (bootstrapFiles == null || bootstrapFiles.isEmpty()) {
            bootstrapFiles = List.of("AGENTS.md", "SOUL.md", "USER.md");
        }

        for (String bootstrapFile : bootstrapFiles) {
            Path file = workspaceRoot.resolve(bootstrapFile);
            if (Files.exists(file)) {
                try {
                    context.append("# ").append(bootstrapFile).append("\n\n");
                    context.append(Files.readString(file));
                    context.append("\n\n");
                } catch (IOException e) {
                    // Skip if cannot read
                }
            }
        }

        return context.toString();
    }
}
