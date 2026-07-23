package com.oryxos;

import java.util.List;
import java.util.Map;

/**
 * Profile - Agent's runtime configuration
 */
public record Profile(
    String name,
    String description,
    Identity identity,
    ProviderConfig provider,
    List<String> tools,
    List<String> skills,
    List<String> mcpServers,
    List<ChannelConfig> channels,
    List<NotifyChannel> notifyChannels,
    List<Schedule> schedules,
    List<String> bootstrap,
    Settings settings
) {
    public record Identity(String agentName, String prompt) {}

    public record ProviderConfig(String name, String model, Double temperature) {}

    public record ChannelConfig(String name, Map<String, String> config) {}

    public record NotifyChannel(String type, Map<String, String> config) {}

    public record Schedule(String cron, String timezone, String message) {}

    public record Settings(int maxIterations, int maxHistoryTurns) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private Identity identity;
        private ProviderConfig provider;
        private List<String> tools = List.of();
        private List<String> skills = List.of();
        private List<String> mcpServers = List.of();
        private List<ChannelConfig> channels = List.of();
        private List<NotifyChannel> notifyChannels = List.of();
        private List<Schedule> schedules = List.of();
        private List<String> bootstrap = List.of();
        private Settings settings = new Settings(10, 20);

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder identity(Identity identity) { this.identity = identity; return this; }
        public Builder provider(ProviderConfig provider) { this.provider = provider; return this; }
        public Builder tools(List<String> tools) { this.tools = tools; return this; }
        public Builder skills(List<String> skills) { this.skills = skills; return this; }
        public Builder mcpServers(List<String> mcpServers) { this.mcpServers = mcpServers; return this; }
        public Builder channels(List<ChannelConfig> channels) { this.channels = channels; return this; }
        public Builder notifyChannels(List<NotifyChannel> notifyChannels) { this.notifyChannels = notifyChannels; return this; }
        public Builder schedules(List<Schedule> schedules) { this.schedules = schedules; return this; }
        public Builder bootstrap(List<String> bootstrap) { this.bootstrap = bootstrap; return this; }
        public Builder settings(Settings settings) { this.settings = settings; return this; }

        public Profile build() {
            return new Profile(name, description, identity, provider, tools, skills,
                mcpServers, channels, notifyChannels, schedules, bootstrap, settings);
        }
    }
}
