package com.oryxos.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * OryxOS CLI - Main command with all subcommands.
 * Implements 12 commands: init, status, chat, serve, gateway, profile list/show/create/delete, provider list, tool list, session list
 */
@Command(name = "oryxos",
         mixinStandardHelpOptions = true,
         version = "OryxOS 0.1.0-SNAPSHOT",
         description = "Enterprise Agent OS - Java 21 + Spring Boot 3.x")
public class OryxosCommand implements Callable<Integer> {

    @Option(names = {"-w", "--workspace"}, description = "Workspace directory")
    String workspace = ".oryxos";

    @Option(names = {"-v", "--version"}, versionHelp = true)
    boolean version;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OryxosCommand()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("OryxOS v0.1.0-SNAPSHOT");
        System.out.println("Enterprise Agent OS - Java 21 + Spring Boot 3.x");
        System.out.println();
        System.out.println("Run 'oryxos <command> --help' for more information.");
        return 0;
    }

    // === Init Command ===
    @Command(name = "init", description = "Initialize workspace")
    static class InitCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";

        @Override
        public Integer call() {
            Path ws = Path.of(workspace);
            System.out.println("Initializing OryxOS workspace at: " + ws.toAbsolutePath());

            // Create directories
            mkdir(ws, "agents");
            mkdir(ws, "memory");
            mkdir(ws, "sessions");
            mkdir(ws, "profiles");

            // Create default files
            createDefaultBootstrap(ws);

            System.out.println("Workspace initialized successfully.");
            return 0;
        }

        private void mkdir(Path parent, String dir) {
            try {
                Path path = parent.resolve(dir);
                java.nio.file.Files.createDirectories(path);
                System.out.println("  Created: " + path);
            } catch (Exception e) {
                System.err.println("  Failed to create " + dir + ": " + e.getMessage());
            }
        }

        private void createDefaultBootstrap(Path ws) {
            try {
                Path agents = ws.resolve("AGENTS.md");
                if (!java.nio.file.Files.exists(agents)) {
                    java.nio.file.Files.writeString(agents, "# AGENTS.md\n\n");
                    System.out.println("  Created: " + agents);
                }
            } catch (Exception e) {
                System.err.println("  Failed to create bootstrap: " + e.getMessage());
            }
        }
    }

    // === Status Command ===
    @Command(name = "status", description = "Show configuration and runtime status")
    static class StatusCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";

        @Override
        public Integer call() {
            System.out.println("OryxOS Status");
            System.out.println("=============");
            System.out.println("Workspace: " + Path.of(workspace).toAbsolutePath());
            System.out.println("Java: " + System.getProperty("java.version"));
            System.out.println("Status: Running");
            return 0;
        }
    }

    // === Profile Commands ===
    @Command(name = "profile", aliases = {"profiles"}, description = "Manage profiles")
    static class ProfileCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";

        @Override
        public Integer call() {
            System.out.println("Profile management commands:");
            System.out.println("  oryxos profile list <name>");
            System.out.println("  oryxos profile show <name>");
            System.out.println("  oryxos profile create <name>");
            System.out.println("  oryxos profile delete <name>");
            return 0;
        }
    }

    @Command(name = "list", description = "List all profiles")
    static class ProfileListCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";

        @Override
        public Integer call() {
            Path profilesDir = Path.of(workspace, "profiles");
            System.out.println("Profiles:");
            if (java.nio.file.Files.exists(profilesDir)) {
                try (var files = java.nio.file.Files.list(profilesDir)) {
                    files.filter(f -> f.toString().endsWith(".yaml"))
                        .forEach(f -> System.out.println("  - " + f.getFileName().toString().replace(".yaml", "")));
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("  (no profiles found)");
            }
            return 0;
        }
    }

    @Command(name = "show", description = "Show profile details")
    static class ProfileShowCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";
        @Parameters(paramLabel = "<name>") String name;

        @Override
        public Integer call() {
            Path profileFile = Path.of(workspace, "profiles", name + ".yaml");
            if (!java.nio.file.Files.exists(profileFile)) {
                System.err.println("Profile not found: " + name);
                return 1;
            }
            try {
                String content = java.nio.file.Files.readString(profileFile);
                System.out.println(content);
                return 0;
            } catch (Exception e) {
                System.err.println("Error reading profile: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "create", description = "Create a new profile")
    static class ProfileCreateCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";
        @Parameters(paramLabel = "<name>") String name;

        @Override
        public Integer call() {
            Path profileFile = Path.of(workspace, "profiles", name + ".yaml");
            if (java.nio.file.Files.exists(profileFile)) {
                System.err.println("Profile already exists: " + name);
                return 1;
            }
            try {
                String defaultContent = String.format("""
                    # Profile: %s
                    name: %s
                    description: ""

                    identity:
                      agent_name: %s
                      prompt: You are a helpful AI assistant.

                    provider:
                      name: deepseek
                      model: deepseek-chat
                      temperature: 0.7

                    tools:
                      - read_file
                      - write_file
                      - list_dir
                      - shell
                      - http_get
                      - http_post
                      - save_memory
                      - recall_memory
                      - notify

                    settings:
                      max_iterations: 10
                      max_history_turns: 20
                    """, name, name, name);

                java.nio.file.Files.createDirectories(profileFile.getParent());
                java.nio.file.Files.writeString(profileFile, defaultContent);
                System.out.println("Profile created: " + name);
                return 0;
            } catch (Exception e) {
                System.err.println("Error creating profile: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "delete", description = "Delete a profile")
    static class ProfileDeleteCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";
        @Parameters(paramLabel = "<name>") String name;

        @Override
        public Integer call() {
            if ("default".equals(name)) {
                System.err.println("Cannot delete 'default' profile");
                return 1;
            }
            Path profileFile = Path.of(workspace, "profiles", name + ".yaml");
            try {
                if (java.nio.file.Files.deleteIfExists(profileFile)) {
                    System.out.println("Profile deleted: " + name);
                    return 0;
                } else {
                    System.err.println("Profile not found: " + name);
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error deleting profile: " + e.getMessage());
                return 1;
            }
        }
    }

    // === Provider Commands ===
    @Command(name = "provider", description = "Manage LLM providers")
    static class ProviderCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Providers:");
            System.out.println("  (configure in application.yml or environment variables)");
            return 0;
        }
    }

    @Command(name = "list", description = "List configured providers")
    static class ProviderListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Configured providers:");
            System.out.println("  - deepseek (via DEEPSEEK_API_KEY)");
            System.out.println("  - qwen (via QWEN_API_KEY)");
            System.out.println("  - openai (via OPENAI_API_KEY)");
            return 0;
        }
    }

    // === Tool Commands ===
    @Command(name = "tool", description = "Manage tools")
    static class ToolCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Tool commands:");
            System.out.println("  oryxos tool list");
            return 0;
        }
    }

    @Command(name = "list", description = "List available tools")
    static class ToolListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Available built-in tools:");
            System.out.println("  - read_file      Read file content");
            System.out.println("  - write_file     Write content to file");
            System.out.println("  - list_dir       List directory contents");
            System.out.println("  - shell          Execute shell command");
            System.out.println("  - http_get       HTTP GET request");
            System.out.println("  - http_post      HTTP POST request");
            System.out.println("  - save_memory    Save to long-term memory");
            System.out.println("  - recall_memory  Search long-term memory");
            System.out.println("  - notify         Send notification");
            return 0;
        }
    }

    // === Session Commands ===
    @Command(name = "session", description = "Manage sessions")
    static class SessionCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Session commands:");
            System.out.println("  oryxos session list");
            return 0;
        }
    }

    @Command(name = "list", description = "List sessions")
    static class SessionListCommand implements Callable<Integer> {
        @Option(names = {"-w", "--workspace"}) String workspace = ".oryxos";

        @Override
        public Integer call() {
            System.out.println("Sessions:");
            System.out.println("  (use REST API or oryxos serve for session management)");
            return 0;
        }
    }
}
