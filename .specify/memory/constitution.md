<!--
同步影响报告
============
版本变更：0.0.0 → 1.0.0（首次批准）
修改的原则：N/A（全部新增）
新增章节：
  - 核心原则（10条原则，覆盖 CLAUDE.md 中的 C1-C10）
  - 补充约束（技术栈与合规）
  - 开发工作流（模块结构与 Agent 定义）
  - 治理（修订与合规）
模板待更新：⚠ 需手动确认
  - .specify/templates/plan-template.md（Constitution Check 部分引用占位符）
  - .specify/templates/spec-template.md（无宪法引用）
  - .specify/templates/tasks-template.md（无宪法引用）
待办事项：
  - TODO(RATIFICATION_DATE)：首次宪法采用 - 设为 2026-07-25
  - .specify/extensions.yml 中的扩展钩子已对齐（git hooks 存在）
-->

# OryxOS 宪法

> 本文件是 OryxOS 项目的核心指南，定义技术栈、模块结构、不可违背原则、数据模型、ReAct Loop 机制、Tool 体系、API、CLI 等关键信息。所有 AI 辅助开发任务必须遵循本文档。

## 核心原则

### 一、技术栈强制要求

OryxOS 必须使用 **JDK 21+** 和 **Spring Boot 3.x**。这些版本不可妥协，**不允许降级**。任何替代版本提案必须拒绝。

**理由**：项目基于 Java 21 虚拟线程和 Spring Boot 3.x 特性设计，降级将破坏根基。

### 二、同步执行模型（禁止响应式堆栈）

系统必须使用同步执行模型。**禁止在代码库中引入 Reactor / WebFlux / CompletableFuture**。

**理由**：保持简单性和可调试性。异步/响应式对此场景无益。

### 三、自实现 ReAct Loop（禁止 Spring AI Agent 抽象）

Agent 核心必须自己实现 ReAct Loop。**必须禁用 Spring AI 的 Agent 抽象**；工具调度完全由 `ReActLoop` + `ToolExecutor` 控制。

**理由**：CLAUDE.md C3 — Spring AI 仅用于 Provider 抽象 + 协议转换 + `@Tool` schema 生成。自动工具执行被禁止。

### 四、Spring AI 仅用一半（仅用 Provider 抽象）

Spring AI 的使用限制为：
- Provider 抽象 + 协议转换
- `@Tool` schema 生成

**禁止使用**：Spring AI 的自动工具执行、Agent 抽象、或任何自动执行特性。

**理由**：Spring AI 提供 LLM 集成原语，ReAct Loop 是我们自己的实现。

### 五、显式 Provider 映射（禁止类型扫描）

Provider 到 ChatModel 的映射必须维护为显式 `Map<String, ChatModel>`。**禁止使用类型扫描**来发现 Provider。

**理由**：CLAUDE.md C5 — 显式控制可用模型，避免意外注册。

### 六、Sandbox 接口先行（应用层白名单）

Sandbox 抽象接口必须在任何沙箱实现之前存在。核心阶段必须使用 `WhitelistSandbox`（应用层路径/模式白名单）。**禁止使用 SecurityManager**。

**理由**：CLAUDE.md C6 — 沙箱接口为文件、Shell 和 HTTP 操作提供控制点。

### 七、审计 day one（工具调用与 LLM 调用必须落库）

`tool_invocations` 和 `llm_calls` 表必须在核心阶段就写入 SQLite。审计日志**不是可选的**——它是 day one 的要求。

**理由**：CLAUDE.md C7 — 可观测性和合规从一开始就需要审计数据。

### 八、敏感配置仅走环境变量

API 密钥、令牌和密钥**禁止写入** YAML 文件、源代码或 Git 提交记录。**所有敏感配置必须来自环境变量**。

**理由**：CLAUDE.md C8 — 防止凭证泄露到部署环境和提交记录中。

### 九、一个目录 = 一个 Agent（Agent 定义布局）

Agent 定义必须位于 `.oryxos/agents/<name>/` 目录中。每个 Agent 目录包含 `AGENT.md`（frontmatter Profile + 正文指令）、可选的 `skills/`、`scripts/` 和 `REFERENCE.md`。Profile 从 Agent 定义派生。

**理由**：CLAUDE.md C9 — Agent 之间清晰隔离，每个 Agent 有自己的定义范围。

### 十、Memory Service 统一门面（三层架构）

`MemoryService` 必须是所有内存访问的统一门面：
- 会话内存（对话历史）
- 长期记忆（`.oryxos/memory/MEMORY.md`）
- 工作内存（中间计算）

**禁止**不通过 `MemoryService` 单独访问这些内存层。

**理由**：CLAUDE.md C10 — 一致的内存访问模式和抽象边界。

## 补充约束

### 技术栈

| 组件 | 技术 | 版本要求 |
|------|------|---------|
| JDK | Java | 21+ |
| 框架 | Spring Boot | 3.x |
| LLM | Spring AI Alibaba | 仅用 Provider 抽象 |
| HTTP | Spring MVC + 虚拟线程 | Java 21 |
| CLI | Picocli | 最新稳定版 |
| 持久化 | SQLite + Spring Data JPA | - |
| 配置 | SnakeYAML | - |
| 日志 | Logback + SLF4J | - |

### 模块结构（Maven 多模块）

| 模块 | 职责 |
|------|------|
| `oryxos-core` | 核心抽象：OryxTool、Session、Profile、ContextLoader、AgentLoader、ReActLoop、PromptBuilder、ToolExecutor、AgentService、AgentScheduler |
| `oryxos-provider` | ProviderService、Function Calling 适配器、显式 Provider→ChatModel 映射 |
| `oryxos-memory` | MemoryService 门面、LongTermMemory、MemoryTools（save_memory/recall_memory） |
| `oryxos-tool` | 内置 Tool（File/Shell/Http/Notify）、McpClientService、McpToolAdapter、ToolRegistry、Sandbox 接口 + WhitelistSandbox |
| `oryxos-channel-cli` | CliChannel、`oryxos chat` 命令 |
| `oryxos-web` | WebServer、6 个 ApiController、GlobalExceptionHandler、OpenAPI 文档 |
| `oryxos-storage` | SQLite 持久化、SessionRepository、ToolInvocationRepository、LlmCallRepository |
| `oryxos-cli` | Picocli 入口、12 个子命令、ConfigLoader |
| `oryxos-boot` | Spring Boot 启动类、自动配置、依赖聚合 |

**模块间通过接口通信。新增 Channel 或 Tool 需要新模块，不需要修改 core。**

## 开发工作流

### Agent 定义规范

```
.oryxos/agents/<name>/
├── AGENT.md           # frontmatter（Profile）+ 正文（任务指令）
├── skills/            # 可选子指令（*.md）
├── scripts/           # 可选脚本（*.py、*.sh）
└── REFERENCE.md       # 可选参考文档
```

### ReAct Loop 算法

1. 将用户消息追加到 Session 对话历史
2. 组装 Prompt：system prompt → Bootstrap → Memory → 对话历史 → 可用 Tools
3. 调用 LLM Provider 获取响应
4. 如果**没有** Tool 调用 → 返回最终响应
5. 如果**有** Tool 调用 → 执行 Tool，将结果追加到历史 → 返回步骤 2
6. 达到最大迭代次数（默认 10 次）→ 强制终止

### PromptBuilder 组装顺序

1. System prompt（`AGENT.md` 正文 + Bootstrap 文件，末尾附当前日期时间）
2. Memory 注入（会话历史 + 长期记忆）
3. 对话历史（按 `maxHistoryTurns` 截断）
4. 当前 Profile 可用的 Tool 列表（Function Calling 格式）

### 核心阶段限制

核心阶段**禁止实现**：
- 并行工具调用
- 动态上下文压缩
- Agent 间任务委托
- 流式响应

## 治理

### 修订程序

1. 提案变更必须包含理由文档
2. 变更必须接受所有 10 条原则的合规审查
3. 破坏性变更需要 MAJOR 版本升级
4. 新增原则或重大扩展指导需要 MINOR 版本升级
5. 澄清、文字修正、拼写纠正需要 PATCH 版本升级

### 版本策略

- **MAJOR**：破坏性治理/原则移除或重新定义
- **MINOR**：新增原则/章节或重大扩展指导
- **PATCH**：澄清、文字、拼写修正、非语义改进

### 合规审查

所有 AI 辅助开发任务和 Pull Request 必须验证：
- 技术栈符合原则一、二、四
- Provider 映射是显式的（原则五）
- Sandbox 接口先于实现（原则六）
- 审计表已写入（原则七）
- YAML/代码/提交中无敏感配置（原则八）
- Agent 定义遵循目录规范（原则九）
- 内存访问通过 MemoryService（原则十）

### 参考文档

- [需求文档](docs/DemandAnalysis.md) — 功能需求和非功能需求
- [技术方案](docs/TechnicalSolution.md) — 技术架构和实现细节
- [业界调研](docs/IndustryResearch.md) — Agent OS 市场分析
- [AI 编程指南](docs/AiProgrammingGuide.md) — Spec-Kit 实施指引

**版本**：1.0.0 | **批准日期**：2026-07-25 | **最后修订**：2026-07-25
