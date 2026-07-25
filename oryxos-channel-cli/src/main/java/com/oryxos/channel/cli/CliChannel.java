package com.oryxos.channel.cli;

import com.oryxos.AgentService;
import com.oryxos.Profile;
import com.oryxos.Session;

import java.util.Scanner;

/**
 * CLI Channel - interactive command-line interface for Agent communication.
 */
public class CliChannel {

    private final AgentService agentService;
    private final Profile profile;
    private final Scanner scanner;

    public CliChannel(AgentService agentService, Profile profile) {
        this.agentService = agentService;
        this.profile = profile;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Start interactive chat loop.
     */
    public void start() {
        System.out.println("OryxOS Chat");
        System.out.println("==========");
        System.out.println("Agent: " + profile.identity().agentName());
        System.out.println("Type 'exit' or 'quit' to end the conversation.");
        System.out.println();

        // Create session
        Session session = agentService.createSession(profile.name(), "cli", "cli-user");

        System.out.println("Session: " + session.getSessionId());
        System.out.println();

        // Chat loop
        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            // Process message
            try {
                String response = agentService.process(session.getSessionId(), input);
                System.out.println("Agent: " + response);
                System.out.println();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Send single message and return response (non-interactive).
     */
    public String sendMessage(String message) {
        Session session = agentService.createSession(profile.name(), "cli", "cli-user");
        return agentService.process(session.getSessionId(), message);
    }
}
