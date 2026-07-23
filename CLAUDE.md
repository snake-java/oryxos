# OryxOS 项目指南

> 本文件是 OryxOS 项目的核心指南，定义技术栈、模块结构、不可违背原则、数据模型、ReAct Loop 机制、Tool 体系、API、CLI 等关键信息。所有 AI 辅助开发任务必须遵循本文档。

---

## 1. 项目概述

OryxOS 是基于 **Java 21 + Spring Boot 3.x** 的企业级 Agent OS，作为统一底座运行多个业务 Agent，共享：渠道接入、模型路由、工具调用、记忆系统、沙箱执行。

**核心定位**：
- 私有部署，数据完全留在企业基础设施
- 不锁任何云生态
- 基于 Spring AI Alibaba 做 LLM 调用
- 自实现 ReAct loop，不依赖 Spring AI 的自动 tool 执行

**交付分两段**：
1. **核心阶段**：Agent OS 运行时内核（地基）
2. **扩展阶段**：企业级治理层（多租户、SSO、完整审计、Tool Policy）

---

## 2. 技术栈

| 组件 | 技术选型 |
|------|---------|
| JDK | 21+ |
| Framework | Spring Boot 3.x |
| LLM | Spring AI Alibaba（Provider 抽象 + 协议转换 + `@Tool` schema 生成） |
| Agent 核心 | 自实现 ReAct Loop |
| HTTP 服务 | Spring MVC + Java 21 Virtual Thread |
| CLI | Picocli |
| 持久化 | SQLite + Spring Data JPA |
| 配置文件 | SnakeYAML |
| 日志 | Logback + SLF4J |
| 指标 | Micrometer + Prometheus（扩展阶段） |

---

## 3. 模块结构（Maven 多模块）

| 模块名 | 职责 |
|--------|------|
| `oryxos-core` | 核心抽象：`OryxTool` 接口、`Session`、`Profile`、`ContextLoader`、`AgentLoader`、`ReActLoop`、`PromptBuilder`、`ToolExecutor`、`AgentService`、`AgentScheduler` |
| `oryxos-provider` | 核心能力一：`ProviderService`、Function Calling 适配、Provider name → `ChatModel` 显式映射 |
| `oryxos-memory` | 核心能力三：`MemoryService` 统一门面、`LongTermMemory`、`MemoryTools`（`save_memory`/`recall_memory`） |
| `oryxos-tool` | 核心能力四：内置 Tool（File/Shell/Http/Notify）、`McpClientService`、`McpToolAdapter`、`ToolRegistry`、`Sandbox` 接口 + `WhitelistSandbox` 实现 |
| `oryxos-channel-cli` | CLI Channel：`CliChannel`、`oryxos chat` 命令 |
| `oryxos-web` | 核心能力五：`WebServer`、6 个 `ApiController`、`GlobalExceptionHandler`、OpenAPI 文档 |
| `oryxos-storage` | 持久化层：SQLite、`SessionRepository`、`ToolInvocationRepository`、`LlmCallRepository` |
| `oryxos-cli` | 命令行入口：Picocli 主入口、12 个子命令、`ConfigLoader` |
| `oryxos-boot` | Spring Boot 启动模块：主类、自动配置、依赖聚合 |

**模块间通过接口解耦。** 扩展阶段加新 Channel 或新 Tool 只加新模块不改 core。

---

## 4. 不可违背原则（Constitution）

以下原则是项目的宪法，任何 AI 辅助开发任务必须遵守，**不允许自行修改**：

| # | 原则 | 说明 |
|---|------|------|
| **C1** | JDK 21 + Spring Boot 3.x | 不得降版本 |
| **C2** | 同步执行模型 | 不引入 Reactor / WebFlux / CompletableFuture |
| **C3** | 自实现 ReAct Loop | 禁用 Spring AI 的 Agent 抽象 |
| **C4** | Spring AI 只用一半 | 只用 Provider 抽象 + 协议转换 + `@Tool` schema 生成；**禁用自动 tool 执行**；tool 调度完全由 `ReActLoop` + `ToolExecutor` 控制 |
| **C5** | Provider 显式映射 | 维护 `Map<String, ChatModel>`，不靠类型扫描 |
| **C6** | Sandbox 接口先行 | `Sandbox` 抽象接口 + `WhitelistSandbox`（应用层白名单），不使用 `SecurityManager` |
| **C7** | 审计 day one 落库 | `tool_invocations` 和 `llm_calls` 核心阶段就写入 SQLite |
| **C8** | 敏感配置只走环境变量 | API key/token 不得明文写入 YAML/代码/提交记录 |
| **C9** | 一个目录 = 一个 Agent | Agent 定义在 `.oryxos/agents/<name>/` 目录，派生 Profile |
| **C10** | Memory 三层统一门面 | `MemoryService` 统一收口会话记忆 + 长期记忆，不分别访问 |

---

## 5. 数据模型

### 5.1 Session（SQLite）

| 字段 | 类型 | 说明 |
|------|------|------|
| `session_id` | VARCHAR | 主键，channel+user+profile 联合生成 |
| `profile_name` | VARCHAR | 关联的 Profile |
| `channel` | VARCHAR | 接入渠道 |
| `user_id` | VARCHAR | 用户标识 |
| `messages_json` | TEXT | JSON 序列化的对话历史 |
| `status` | VARCHAR | `active` / `archived` |
| `created_at` | TIMESTAMP | 创建时间 |
| `last_active_at` | TIMESTAMP | 最后活跃时间 |
| `archived_at` | TIMESTAMP | 归档时间（可空） |

### 5.2 Tool Invocation（SQLite）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `session_id` | VARCHAR | 关联 Session |
| `tool_name` | VARCHAR | Tool 名称 |
| `input_json` | TEXT | 调用参数 |
| `result_json` | TEXT | 执行结果 |
| `success` | BOOLEAN | 是否成功 |
| `error_message` | TEXT | 错误信息（可空） |
| `duration_ms` | BIGINT | 执行耗时 |
| `created_at` | TIMESTAMP | 调用时间 |

### 5.3 LLM Call（SQLite）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `session_id` | VARCHAR | 关联 Session |
| `provider` | VARCHAR | Provider 名称 |
| `model` | VARCHAR | 模型名 |
| `prompt_tokens` | INT | 输入 token 数 |
| `completion_tokens` | INT | 输出 token 数 |
| `total_tokens` | INT | 总 token 数 |
| `duration_ms` | BIGINT | 调用耗时 |
| `created_at` | TIMESTAMP | 调用时间 |

### 5.4 Scheduled Tasks（SQLite，第 28 节）

| 字段 | 类型 | 说明 |
|------|------|------|
| `task_id` | VARCHAR | 主键 |
| `profile_name` | VARCHAR | 归属 Profile |
| `cron` | VARCHAR | cron 表达式 |
| `zone` | VARCHAR | 时区 |
| `message` | TEXT | 到点发给 Agent 的消息 |
| `enabled` | BOOLEAN | 是否启用 |
| `next_run_at` | TIMESTAMP | 下次触发时刻 |
| `last_run_at` | TIMESTAMP | 上次触发时刻 |
| `last_status` | VARCHAR | 上次结果 |
| `run_count` | INT | 累计触发次数 |

### 5.5 Task Executions（SQLite，第 28 节）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `task_id` | VARCHAR | 关联 `scheduled_tasks` |
| `session_id` | VARCHAR | 本次触发所用的 Session |
| `started_at` | TIMESTAMP | 开始时间 |
| `success` | BOOLEAN | 是否成功 |
| `error_message` | TEXT | 失败信息（可空） |
| `duration_ms` | BIGINT | 执行耗时 |

### 5.6 Memory（文件系统）

- 位置：`.oryxos/memory/MEMORY.md`
- 格式：Markdown 文件，按 `## 核心记忆` / `## 归档记忆` 两个 header 分区
- 每条记忆带日期 header

---

## 6. ReAct Loop 机制

ReAct（Reason + Act）是 Agent 的核心工作机制。

**算法步骤**：
1. 接到用户消息追加到 Session 对话历史
2. 组装 Prompt（system prompt + Bootstrap + Memory + 对话历史 + 可用 Tool 列表）
3. 调用 LLM Provider 获取响应
4. 如果**没有** Tool 调用 → 返回最终响应
5. 如果**有** Tool 调用 → 执行 Tool，把结果追加到对话历史 → 回到步骤 2
6. 达到最大迭代次数（默认 10 次）强制结束

**PromptBuilder 组装顺序**：
1. system prompt（`AGENT.md` 正文 + Bootstrap 文件，末尾附当前日期时间）
2. Memory 注入（会话历史 + 长期记忆）
3. 对话历史（按 `maxHistoryTurns` 截断）
4. 当前 Profile 可用的 Tool 列表（Function Calling 格式）

**核心阶段不做**：Tool 调用并行、上下文动态压缩、Agent 间任务委托、流式响应。

---

## 7. Tool 体系

### 7.1 内置 Tool（9 个）

| Tool | 类型 | 说明 |
|------|------|------|
| `read_file` | 文件 | 读取文件内容，受路径白名单限制 |
| `write_file` | 文件 | 写入文件内容，受路径白名单限制 |
| `list_dir` | 文件 | 列出目录，受路径白名单限制 |
| `shell` | Shell | 执行 bash 命令，带超时和命令白名单 |
| `http_get` | HTTP | 发起 GET 请求，有域名白名单限制 |
| `http_post` | HTTP | 发起 POST 请求，有域名白名单限制 |
| `save_memory` | Memory | 把内容追加到 MEMORY.md |
| `recall_memory` | Memory | 按关键词检索 MEMORY.md |
| `notify` | Notify | 推送消息到通知渠道 |

### 7.2 Plugin Tool 三档接入

| 方式 | 门槛 | 做法 |
|------|------|------|
| 方式一 | 零代码（主推） | 写 SKILL.md + 复用社区现成 MCP server |
| 方式二 | 轻代码 | 用任何语言写 MCP server |
| 方式三 | 重代码 | 用 `@Tool` 注解写 Java Spring Bean |

### 7.3 Sandbox

**接口**：
```java
Sandbox.enforce(SandboxAction action)
SandboxAction = { type: ActionType, target: String }
ActionType = FILE_READ | FILE_WRITE | SHELL_COMMAND | HTTP_REQUEST
```

**核心阶段实现**：`WhitelistSandbox`（应用层 Path/Pattern 白名单）

---

## 8. Agent 定义

### 8.1 一个目录 = 一个 Agent

Agent 定义在 `.oryxos/agents/<name>/` 目录：

```
.oryxos/agents/<name>/
├── AGENT.md           # frontmatter（Profile）+ 正文（任务指令）
├── skills/            # 可选子指令
│   └── *.md
├── scripts/          # 可选脚本
│   └── *.py / *.sh
└── REFERENCE.md       # 可选参考文档
```

### 8.2 AGENT.md frontmatter

```yaml
name: string
description: string
provider:
  name: string        # deepseek/qwen/kimi 等
  model: string
tools:
  - string
notify_channels:
  - type: string      # webhook/feishu 等
    url: string
schedules:
  - cron: string
    timezone: string
    message: string
```

### 8.3 上下文加载

- `AGENT.md` 正文 + Bootstrap 文件（`AGENTS.md`、`SOUL.md`、`USER.md`）→ system prompt
- 子指令/脚本/参考 **不预载**，按需经 `read_file`/`shell` 取用

---

## 9. API（REST）

### 核心阶段 10 个端点

**会话管理（4 个）**：
- `POST /api/v1/sessions` — 创建会话
- `POST /api/v1/sessions/{id}/messages` — 发消息
- `GET /api/v1/sessions/{id}` — 查历史
- `DELETE /api/v1/sessions/{id}` — 归档会话

**Agent 调用（1 个）**：
- `POST /api/v1/agents/{name}/invoke` — 无状态调用

**信息查询（3 个）**：
- `GET /api/v1/profiles` — 列 Profile
- `GET /api/v1/memory` — 查长期记忆
- `GET /api/v1/tools` — 列可用 Tool

**系统状态（2 个）**：
- `GET /api/v1/health` — 健康检查
- `GET /api/v1/info` — 运行信息

**扩展阶段补齐**：Agent CRUD、Memory append/clear/search、Tool describe、LLM 统计、Schedule 管理、SSE 流式响应。

---

## 10. CLI 命令

| 命令 | 说明 |
|------|------|
| `oryxos init` | 初始化工作区 |
| `oryxos status` | 查看配置和运行状态 |
| `oryxos chat [--profile <name>]` | 交互对话 |
| `oryxos serve` | 启动 HTTP API 服务 |
| `oryxos gateway` | 启动多渠道守护进程 |
| `oryxos profile list` | 列出所有 Profile |
| `oryxos profile create <name>` | 创建新 Profile |
| `oryxos profile show <name>` | 查看 Profile 详情 |
| `oryxos profile delete <name>` | 删除 Profile |
| `oryxos provider list` | 列出已配置的 Provider |
| `oryxos tool list` | 列出已注册的 Tool |
| `oryxos session list` | 列出会话历史 |

---

## 11. 工作区结构

```
.oryxos/
├── agents/            # 每个子目录 = 一个 Agent
├── memory/
│   └── MEMORY.md      # 长期记忆
├── mcp_servers.yaml   # MCP 配置
├── sessions/          # Session 数据
├── logs/              # 日志
├── AGENTS.md          # Bootstrap：项目级 agent 行为说明
├── SOUL.md            # Bootstrap：默认 agent 人格
├── USER.md            # Bootstrap：用户偏好
└── oryxos.db          # SQLite
```

---

## 12. 常见陷阱

| 陷阱 | 正确做法 |
|------|---------|
| 启用 Spring AI 自动 tool 执行 | 必须禁用，tool 调度由 `ToolExecutor` 控制 |
| Provider 用类型扫描 | 必须维护显式 `Map<String, ChatModel>` |
| 把 `AgentLoader`/`AGENT.md` 当成 Tool | Agent 目录归 `ContextLoader`，在 core 模块 |
| 把 Tool 拆成多个模块 | 合并为 `oryxos-tool` 一个模块 |
| 审计表没落库 | `tool_invocations` 和 `llm_calls` day one 写入 |
| 敏感配置明文 | 只走环境变量，不写入 YAML/代码 |
| 把 Memory 简化成跟 Session 合并 | `MemoryService` 三层统一门面 |

---

## 13. 参考文档

- [需求文档](docs/DemandAnalysis.md) — 功能需求和非功能需求
- [技术方案](docs/TechnicalSolution.md) — 技术架构和实现细节
- [业界调研](docs/IndustryResearch.md) — Agent OS 市场分析
- [AI 编程指南](docs/AiProgrammingGuide.md) — Spec-Kit 实施指引

---

*本文档由 AI 辅助生成，所有 AI 开发任务必须遵循本文档定义的技术栈、模块结构、不可违背原则和数据模型。*
