package com.oryxos.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * OryxOS CLI - Main entry point
 */
@Command(name = "oryxos", mixinStandardHelpOptions = true, version = "OryxOS 0.1.0-SNAPSHOT")
public class OryxCli implements Runnable {

    @Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version")
    boolean version;

    @Option(names = {"-w", "--workspace"}, description = "Workspace directory")
    String workspace = ".oryxos";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OryxCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("OryxOS v0.1.0-SNAPSHOT");
        System.out.println("Enterprise Agent OS - Java 21 + Spring Boot 3.x");
        System.out.println();
        System.out.println("Usage: oryxos <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  init              Initialize workspace");
        System.out.println("  chat              Start interactive chat");
        System.out.println("  serve             Start HTTP API server");
        System.out.println("  profile           Manage profiles");
        System.out.println("  session           Manage sessions");
        System.out.println();
        System.out.println("Run 'oryxos <command> --help' for more information.");
    }
}
