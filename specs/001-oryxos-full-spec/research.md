# Research: OryxOS 企业级 Agent OS

**Date**: 2026-07-25
**Feature**: 001-oryxos-full-spec
**Status**: Complete

## R1: Spring AI Alibaba Provider 抽象最佳实践

**Decision**: 使用 Spring AI Alibaba 的 `ChatClient` + 自定义 `ProviderService` 封装

**Rationale**: Spring AI Alibaba 已实现主流 LLM（DeepSeek、通义、Kimi 等）的 connector，直接使用其 `ChatClient` 是最小成本方案。Provider 抽象层自行维护 `Map<String, ChatModel>` 显式映射，遵循宪法原则五。

**Alternatives considered**:
- 直接使用各厂商 SDK → 维护成本高，不选择
- OpenAI Java SDK → 仅支持 OpenAI，丢失多厂商抽象优势

**实现要点**:
```java
@Service
public class ProviderService {
    private final Map<String, ChatModel> providers = new HashMap<>();

    public ChatModel getProvider(String name) {
        return providers.get(name);
    }

    public void registerProvider(String name, ChatModel model) {
        providers.put(name, model);
    }
}
```

---

## R2: Picocli 与 Spring Boot 集成模式

**Decision**: Picocli 作为独立 CLI 模块，通过 Spring Boot 自动配置集成

**Rationale**: Picocli 支持 Java 21 虚拟线程和子命令，适合复杂 CLI 场景。Spring Boot 的自动配置机制可以复用业务逻辑（AgentService 等）。

**Alternatives considered**:
- Spring Shell → 与 Spring AI 生态绑定，不选择
- JCommander → 子命令支持弱，不选择

**实现要点**:
- `oryxos-cli` 模块包含 Picocli `@Command` 类
- 通过 Spring Boot `CommandLineRunner` 触发
- 共享 `oryxos-core` 中的服务

---

## R3: SQLite + Spring Data JPA 集成

**Decision**: 使用 SQLite + Spring Data JPA（Hibernate 实现）

**Rationale**: SQLite 嵌入式数据库适合单节点部署，无需额外部署数据库服务。Spring Data JPA 提供标准化 CRUD 接口。

**Alternatives considered**:
- H2 → 纯 Java 实现，但 SQLite 在生产环境更可靠
- PostgreSQL → 需要额外部署，不适合核心阶段

**实现要点**:
- 使用 `sqlite-jdbc` 驱动
- Spring Data JPA Repository 模式
- 实体类映射到现有表结构（Session、ToolInvocation、LlmCall）

---

## R4: Micrometer + Prometheus 指标暴露

**Decision**: Micrometer + Prometheus Registry + `/actuator/prometheus` 端点

**Rationale**: Micrometer 是 Spring Boot 3.x 标准指标库，Prometheus 是云原生监控事实标准。Spring Boot Actuator 提供开箱即用的端点集成。

**实现要点**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
```

**指标定义**:
- `oryxos_llm_calls_total` (counter)
- `oryxos_tool_invocations_total` (counter)
- `oryxos_active_sessions` (gauge)
- `oryxos_llm_call_duration_seconds` (histogram)
- `oryxos_tool_call_duration_seconds` (histogram)

---

## R5: Logback 结构化 JSON 日志配置

**Decision**: Logback + logstash-logback-encoder 输出结构化 JSON

**Rationale**: logstash-logback-encoder 是 Logback 的 JSON 编码器，支持自动添加 trace ID、时间戳等字段，便于 ELK/Grafana Loki 收集。

**实现要点**:
```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <includeMdcKeyName>traceId</includeMdcKeyName>
    <includeMdcKeyName>sessionId</includeMdcKeyName>
</encoder>
```

**MDC 字段**:
- `traceId`: 请求唯一标识
- `sessionId`: 会话标识
- `agentId`: Agent 标识

---

## R6: ReAct Loop 算法实现要点

**Decision**: 自实现 ReActLoop 类，约 100-150 行 Java 代码

**Rationale**: 宪法原则三要求自实现 ReAct Loop，不使用 Spring AI Agent 抽象。核心循环简单：调用 LLM → 解析 Tool Call → 执行 Tool → 循环。

**算法伪代码**:
```
1. 追加用户消息到 Session
2. 组装 Prompt（PromptBuilder）
3. 调用 LLM Provider
4. 解析 LLM 响应中的 tool_calls
5. 如果无 tool_calls → 返回响应给用户
6. 如果有 tool_calls → 执行每个 Tool（ToolExecutor）
7. 追加 Tool 结果到 Session
8. 如果达到 max_iterations → 强制结束
9. 否则 → 回到步骤 2
```

**关键类**:
- `ReActLoop`: 主循环控制
- `PromptBuilder`: Prompt 组装
- `ToolExecutor`: Tool 调用执行
- `FunctionCallDeserializer`: LLM 响应解析

---

## R7: Sandbox Whitelist 实现模式

**Decision**: 应用层白名单校验，Sandbox 接口 + WhitelistSandbox 实现

**Rationale**: 宪法原则六要求 Sandbox 接口先行，应用层白名单适合核心阶段。JDK 17+ SecurityManager 已废弃，不使用。

**实现要点**:
```java
public interface Sandbox {
    void enforce(SandboxAction action) throws SandboxViolationException;
}

public class WhitelistSandbox implements Sandbox {
    private final PathPattern fileWhitelist;
    private final Set<String> commandWhitelist;
    private final Set<String> domainWhitelist;
}
```

**白名单配置** (Profile 中):
```yaml
sandbox:
  allowed_paths:
    - /tmp/oryxos/**
    - /home/user/oryxos/**
  allowed_commands:
    - git
    - ls
    - cat
  allowed_domains:
    - api.weather.com
    - api.example.com
```

---

## R8: 定时任务调度实现

**Decision**: 使用 `java.util.concurrent.ScheduledExecutorService`

**Rationale**: Java 标准库，无需引入第三方依赖。cron 表达式解析使用 `cron-utils`。

**Alternatives considered**:
- Spring @Scheduled → 与 Spring Boot 绑定，但适合核心阶段
- Quartz → 过于重量，不选择

**实现要点**:
```java
@Service
public class AgentScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void schedule(String cron, String zone, Runnable task) {
        CronDefinition def = CronDefinitionBuilder.instanceDefinitionFor(CRON_TYPE);
        CronParser parser = new CronParser(def);
        Cron cronSchedule = parser.parse(cron);
        // 计算下次执行时间，注册调度任务
    }
}
```

---

## 总结

所有研究课题已完成。以下是关键决策：

| 课题 | 决策 |
|------|------|
| LLM 调用 | Spring AI Alibaba ChatClient + 自封装 ProviderService |
| CLI | Picocli + Spring Boot 自动配置 |
| 持久化 | SQLite + Spring Data JPA |
| 指标 | Micrometer + Prometheus |
| 日志 | Logback + logstash-logback-encoder JSON 格式 |
| ReAct Loop | 自实现 ReActLoop + PromptBuilder + ToolExecutor |
| Sandbox | Sandbox 接口 + WhitelistSandbox 实现 |
| 调度 | ScheduledExecutorService + cron-utils |
