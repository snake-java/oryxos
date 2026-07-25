package com.oryxos.web;

import com.oryxos.AgentService;
import com.oryxos.Profile;
import com.oryxos.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for OryxOS.
 * Implements 10 core endpoints.
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final AgentService agentService;

    public ApiController(AgentService agentService) {
        this.agentService = agentService;
    }

    // === Session Management ===

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        Session session = agentService.createOrResumeSession(
            request.channel(),
            request.userId(),
            request.profileName()
        );
        return ResponseEntity.ok(new SessionResponse(
            session.getSessionId(),
            session.getProfileName(),
            session.getChannel(),
            session.getUserId(),
            session.getStatus(),
            session.getCreatedAt().toString()
        ));
    }

    @PostMapping("/sessions/{id}/messages")
    public ResponseEntity<MessagesResponse> sendMessage(
            @PathVariable String id,
            @RequestBody MessageRequest request) {
        String response = agentService.processMessage(id, request.content());
        Session session = agentService.getSession(id);
        return ResponseEntity.ok(new MessagesResponse(
            id,
            session != null ? session.getMessagesJson() : "[]",
            response
        ));
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<SessionDetailResponse> getSession(@PathVariable String id) {
        Session session = agentService.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SessionDetailResponse(
            session.getSessionId(),
            session.getProfileName(),
            session.getChannel(),
            session.getUserId(),
            session.getStatus(),
            session.getMessagesJson(),
            session.getCreatedAt().toString(),
            session.getLastActiveAt().toString()
        ));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<ArchiveResponse> archiveSession(@PathVariable String id) {
        agentService.archiveSession(id);
        return ResponseEntity.ok(new ArchiveResponse(id, "archived"));
    }

    // === Agent Invocation ===

    @PostMapping("/agents/{name}/invoke")
    public ResponseEntity<InvokeResponse> invokeAgent(
            @PathVariable String name,
            @RequestBody InvokeRequest request) {
        String response = agentService.invokeAgent(name, request.userId(), request.message());
        return ResponseEntity.ok(new InvokeResponse(response, null));
    }

    // === Profile Management ===

    @GetMapping("/profiles")
    public ResponseEntity<ProfilesResponse> listProfiles() {
        List<Profile> profiles = agentService.listProfiles();
        List<ProfileInfo> profileInfos = profiles.stream()
            .map(p -> new ProfileInfo(p.name(), p.description(), p.provider().name(), p.provider().model()))
            .toList();
        return ResponseEntity.ok(new ProfilesResponse(profileInfos));
    }

    // === Memory ===

    @GetMapping("/memory")
    public ResponseEntity<MemoryResponse> getMemory(@RequestParam(required = false) String query) {
        String content = agentService.getMemoryContent(query);
        return ResponseEntity.ok(new MemoryResponse(content));
    }

    // === Tool Information ===

    @GetMapping("/tools")
    public ResponseEntity<ToolsResponse> listTools() {
        String[] tools = agentService.getRegisteredTools();
        return ResponseEntity.ok(new ToolsResponse(List.of(tools)));
    }

    // === System Status ===

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", Map.of(
            "db", Map.of("status", "UP"),
            "llm", Map.of("status", "UP")
        )));
    }

    @GetMapping("/info")
    public ResponseEntity<InfoResponse> info() {
        return ResponseEntity.ok(new InfoResponse(
            "0.1.0-SNAPSHOT",
            "OryxOS",
            System.getProperty("java.version"),
            new String[]{"deepseek", "qwen", "openai"},
            0,
            0
        ));
    }

    // === Record Classes ===

    public record CreateSessionRequest(String profileName, String channel, String userId) {}
    public record SessionResponse(String sessionId, String profileName, String channel, String userId, String status, String createdAt) {}
    public record MessagesResponse(String sessionId, String messages, String lastResponse) {}
    public record MessageRequest(String content, String role) {}
    public record SessionDetailResponse(String sessionId, String profileName, String channel, String userId, String status, String messages, String createdAt, String lastActiveAt) {}
    public record ArchiveResponse(String sessionId, String status) {}
    public record InvokeRequest(String message, String userId) {}
    public record InvokeResponse(String response, String sessionId) {}
    public record ProfileInfo(String name, String description, String providerName, String model) {}
    public record ProfilesResponse(List<ProfileInfo> profiles) {}
    public record MemoryResponse(String content) {}
    public record ToolsResponse(List<String> tools) {}
    public record HealthResponse(String status, Map<String, Map<String, String>> components) {}
    public record InfoResponse(String version, String name, String javaVersion, String[] providers, int activeSessions, long uptimeSeconds) {}
}
