# Implementation Plan: OryxOS 企业级 Agent OS

**Branch**: `001-oryxos-full-spec` | **Date**: 2026-07-25 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-oryxos-full-spec/spec.md`

## Summary

OryxOS 是基于 Java 21 + Spring Boot 3.x 的企业级 Agent OS，作为统一底座运行多个业务 Agent。核心能力包括：LLM Provider 抽象路由、ReAct 循环自实现、三层 Memory 架构、Plugin Tool 三档接入、Web Service REST API、定时任务调度。

**技术路径**：基于 Spring AI Alibaba 做 LLM 调用（仅用 Provider 抽象），自实现 ReAct Loop，SQLite 持久化，Picocli CLI，Logback + SLF4J 日志。

## Technical Context

**Language/Version**: Java 21+

**Primary Dependencies**:
- Spring Boot 3.x (Web MVC, Data JPA)
- Spring AI Alibaba (Provider 抽象 + `@Tool` schema 生成)
- SQLite + Spring Data JPA
- Picocli (CLI)
- SnakeYAML (配置)
- Logback + SLF4J (日志)
- Micrometer + Prometheus (指标)

**Storage**: SQLite (核心阶段)，H2 作为备选

**Testing**: JUnit 5 + Mockito（单元测试），待定集成测试框架

**Target Platform**: Linux server, JVM 21+

**Project Type**: Maven 多模块 Java 应用 / Agent OS

**Performance Goals**:
- 单节点 Agent 数 ≥ 10
- 单节点并发 Session 数 ≥ 100
- Session 创建 P99 延迟 ≤ 200ms
- 工具调用白名单校验延迟 ≤ 10ms

**Constraints**:
- 同步执行模型（禁止 Reactor/WebFlux/CompletableFuture）
- 自实现 ReAct Loop（禁用 Spring AI Agent 抽象）
- 敏感配置仅走环境变量
- 审计 day one 落库

**Scale/Scope**: 17 条功能需求，4 周/12 小时核心阶段

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| 原则 | 检查项 | 状态 |
|------|--------|------|
| 一、技术栈强制要求 | JDK 21+、Spring Boot 3.x | ✅ PASS |
| 二、同步执行模型 | 无 Reactor/WebFlux/CompletableFuture | ✅ PASS |
| 三、自实现 ReAct Loop | ReActLoop + ToolExecutor 自实现 | ✅ PASS |
| 四、Spring AI 仅用一半 | 仅用 Provider 抽象，无自动 tool 执行 | ✅ PASS |
| 五、显式 Provider 映射 | Map<String, ChatModel> 显式维护 | ✅ PASS |
| 六、Sandbox 接口先行 | WhitelistSandbox + Sandbox 接口 | ✅ PASS |
| 七、审计 day one | tool_invocations、llm_calls 写入 SQLite | ✅ PASS |
| 八、敏感配置仅走环境变量 | API key/token 来自环境变量 | ✅ PASS |
| 九、一个目录 = 一个 Agent | .oryxos/agents/<name>/ 结构 | ✅ PASS |
| 十、Memory Service 统一门面 | MemoryService 统一收口三层记忆 | ✅ PASS |

**结论**: 所有宪法原则检查通过，无违规项。

## Project Structure

### Documentation (this feature)

```text
specs/001-oryxos-full-spec/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── api-contracts.md
└── tasks.md             # Phase 2 output (/speckit-tasks)
```

### Source Code (repository root)

OryxOS 采用 Maven 多模块结构，遵循宪法中的模块划分：

```text
oryxos/                          # Git repo root
├── oryxos-core/                 # 核心抽象模块
│   └── src/main/java/
│       └── com/oryxos/core/
│           ├── OryxTool.java
│           ├── Session.java
│           ├── Profile.java
│           ├── ContextLoader.java
│           ├── AgentLoader.java
│           ├── ReActLoop.java
│           ├── PromptBuilder.java
│           ├── ToolExecutor.java
│           ├── AgentService.java
│           └── AgentScheduler.java
├── oryxos-provider/             # LLM Provider 抽象
│   └── src/main/java/
│       └── com/oryxos/provider/
│           ├── ProviderService.java
│           └── ChatModelRegistry.java
├── oryxos-memory/               # Memory 三层架构
│   └── src/main/java/
│       └── com/oryxos/memory/
│           ├── MemoryService.java
│           ├── LongTermMemory.java
│           └── MemoryTools.java
├── oryxos-tool/                 # Tool 体系 + Sandbox
│   └── src/main/java/
│       └── com/oryxos/tool/
│           ├── ToolRegistry.java
│           ├── Sandbox.java
│           ├── WhitelistSandbox.java
│           └── tools/           # 内置 Tool 实现
├── oryxos-channel-cli/          # CLI Channel
│   └── src/main/java/
│       └── com/oryxos/channel/cli/
│           └── CliChannel.java
├── oryxos-web/                  # REST API
│   └── src/main/java/
│       └── com/oryxos/web/
│           ├── ApiController.java
│           └── GlobalExceptionHandler.java
├── oryxos-storage/              # SQLite 持久化
│   └── src/main/java/
│       └── com/oryxos/storage/
│           ├── SessionRepository.java
│           ├── ToolInvocationRepository.java
│           └── LlmCallRepository.java
├── oryxos-cli/                  # Picocli 命令行
│   └── src/main/java/
│       └── com/oryxos/cli/
│           └── OryxosCommand.java
├── oryxos-boot/                 # Spring Boot 启动模块
│   └── src/main/java/
│       └── com/oryxos/boot/
│           └── OryxosApplication.java
├── .oryxos/                     # 工作区（运行时创建）
│   ├── agents/
│   ├── memory/
│   └── sessions/
└── pom.xml                      # 父 POM
```

**Structure Decision**: Maven 多模块项目，模块职责严格按照宪法规定的模块结构划分。核心模块间通过接口通信，扩展通过新增模块实现。

## Phase 0: Research

**待研究课题** (从 Technical Context 中的 NEEDS CLARIFICATION 和未知项提取):

| # | 研究课题 | 驱动需求 | 状态 |
|---|---------|---------|------|
| R1 | Spring AI Alibaba Provider 抽象最佳实践 | FR-003、FR-004 | 待研究 |
| R2 | Picocli 与 Spring Boot 集成模式 | FR-010 | 待研究 |
| R3 | SQLite + Spring Data JPA 集成 | FR-005、FR-013 | 待研究 |
| R4 | Micrometer + Prometheus 指标暴露 | SC-010 | 待研究 |
| R5 | Logback 结构化 JSON 日志配置 | SC-009 | 待研究 |
| R6 | ReAct Loop 算法实现要点 | FR-004 | 待研究 |
| R7 | Sandbox Whitelist 实现模式 | FR-009 | 待研究 |
| R8 | cron-utils 或 ScheduledExecutorService | FR-012 | 待研究 |

## Phase 1: Design & Contracts

**前提**: research.md 完成

### 1.1 Data Model (data-model.md)

基于规格说明书 Key Entities + 宪法数据模型章节:
- Profile (YAML 配置)
- Session (SQLite)
- ToolInvocation (SQLite)
- LlmCall (SQLite)
- ScheduledTask (SQLite)
- TaskExecution (SQLite)
- Memory (文件系统)

### 1.2 API Contracts (contracts/api-contracts.md)

基于 FR-011 定义的 10 个 REST API 端点:
- POST /api/v1/sessions
- POST /api/v1/sessions/{id}/messages
- GET /api/v1/sessions/{id}
- DELETE /api/v1/sessions/{id}
- POST /api/v1/agents/{name}/invoke
- GET /api/v1/profiles
- GET /api/v1/memory
- GET /api/v1/tools
- GET /api/v1/health
- GET /api/v1/info

### 1.3 Quickstart (quickstart.md)

快速入门指南:
- 环境准备（JDK 21、MAVEN）
- oryxos init 初始化
- Profile 配置
- oryxos chat 启动对话
- REST API 调用示例

## Complexity Tracking

> 无复杂度违规项。所有设计决策均符合宪法约束。
