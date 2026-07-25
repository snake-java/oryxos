# Data Model: OryxOS 企业级 Agent OS

**Date**: 2026-07-25
**Feature**: 001-oryxos-full-spec

## 1. 实体概览

| 实体 | 存储 | 说明 |
|------|------|------|
| Profile | YAML 文件 | Agent 运行时宿主配置 |
| Session | SQLite | 用户和 Agent 一次对话的上下文容器 |
| ToolInvocation | SQLite | 记录每次 Tool 调用 |
| LlmCall | SQLite | 记录每次 LLM 调用 |
| ScheduledTask | SQLite | 定时任务配置 |
| TaskExecution | SQLite | 定时任务执行记录 |
| Memory | 文件系统 | 长期记忆（MEMORY.md） |

---

## 2. Profile (YAML 配置文件)

**位置**: `.oryxos/profiles/<name>.yaml`

```yaml
name: string                    # Profile 名称，唯一标识
description: string             # 描述

identity:
  agent_name: string            # Agent 名称
  prompt: string                # 人格/系统提示词（或引用 SOUL.md）

provider:
  name: string                  # Provider 名称（deepseek/qwen/kimi 等）
  model: string                 # 模型名
  temperature: float            # 温度参数（可选，默认 0.7）

tools:
  - string                      # 可用 Tool 名称列表

skills:
  - string                      # 引用的 SKILL.md 文件列表

mcp_servers:
  - string                      # 引用的 MCP Server 列表

channels:
  - name: string                # Channel 名称
    config: {}                  # Channel 配置

bootstrap:
  - string                      # Bootstrap 文件列表

settings:
  max_iterations: 10            # 最大 ReAct 迭代次数
  max_history_turns: 20         # 最大对话历史轮数

sandbox:
  allowed_paths:                # 文件操作白名单路径
    - string
  allowed_commands:             # Shell 命令白名单
    - string
  allowed_domains:              # HTTP 请求白名单域名
    - string
```

---

## 3. Session (SQLite)

**表名**: `sessions`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| session_id | VARCHAR(255) | PRIMARY KEY | 主键，channel+user+profile 联合生成 |
| profile_name | VARCHAR(255) | NOT NULL | 关联的 Profile 名称 |
| channel | VARCHAR(50) | NOT NULL | 接入渠道（cli/http/webhook） |
| user_id | VARCHAR(255) | NOT NULL | 用户标识 |
| messages_json | TEXT | NOT NULL | JSON 序列化的对话历史 |
| status | VARCHAR(20) | NOT NULL | 状态：active / archived |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |
| last_active_at | TIMESTAMP | NOT NULL | 最后活跃时间 |
| archived_at | TIMESTAMP | NULL | 归档时间 |

**状态转换**:
- `active` → `archived`: 用户主动调用 DELETE API 或 24 小时无活动自动归档

---

## 4. ToolInvocation (SQLite)

**表名**: `tool_invocations`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主键 |
| session_id | VARCHAR(255) | NOT NULL, FK(sessions) | 关联 Session |
| tool_name | VARCHAR(100) | NOT NULL | Tool 名称 |
| input_json | TEXT | NOT NULL | 调用参数（JSON） |
| result_json | TEXT | NULL | 执行结果（JSON） |
| success | BOOLEAN | NOT NULL | 是否成功 |
| error_message | TEXT | NULL | 错误信息 |
| duration_ms | BIGINT | NOT NULL | 执行耗时（毫秒） |
| created_at | TIMESTAMP | NOT NULL | 调用时间 |

**索引**: `idx_tool_invocations_session_id(session_id)`

---

## 5. LlmCall (SQLite)

**表名**: `llm_calls`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主键 |
| session_id | VARCHAR(255) | NOT NULL, FK(sessions) | 关联 Session |
| provider | VARCHAR(50) | NOT NULL | Provider 名称 |
| model | VARCHAR(100) | NOT NULL | 模型名 |
| prompt_tokens | INT | NOT NULL | 输入 token 数 |
| completion_tokens | INT | NOT NULL | 输出 token 数 |
| total_tokens | INT | NOT NULL | 总 token 数 |
| duration_ms | BIGINT | NOT NULL | 调用耗时（毫秒） |
| created_at | TIMESTAMP | NOT NULL | 调用时间 |

**索引**: `idx_llm_calls_session_id(session_id)`

---

## 6. ScheduledTask (SQLite)

**表名**: `scheduled_tasks`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| task_id | VARCHAR(255) | PRIMARY KEY | 主键 |
| profile_name | VARCHAR(255) | NOT NULL | 归属 Profile |
| cron | VARCHAR(100) | NOT NULL | cron 表达式 |
| zone | VARCHAR(50) | NOT NULL | 时区（默认 UTC） |
| message | TEXT | NOT NULL | 到点发给 Agent 的消息 |
| enabled | BOOLEAN | NOT NULL | 是否启用 |
| next_run_at | TIMESTAMP | NOT NULL | 下次触发时刻 |
| last_run_at | TIMESTAMP | NULL | 上次触发时刻 |
| last_status | VARCHAR(20) | NULL | 上次结果（success/failure） |
| run_count | INT | NOT NULL DEFAULT 0 | 累计触发次数 |

---

## 7. TaskExecution (SQLite)

**表名**: `task_executions`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主键 |
| task_id | VARCHAR(255) | NOT NULL, FK(scheduled_tasks) | 关联任务 |
| session_id | VARCHAR(255) | NOT NULL, FK(sessions) | 本次触发所用的 Session |
| started_at | TIMESTAMP | NOT NULL | 开始时间 |
| success | BOOLEAN | NOT NULL | 是否成功 |
| error_message | TEXT | NULL | 失败信息 |
| duration_ms | BIGINT | NOT NULL | 执行耗时 |

---

## 8. Memory (文件系统)

**位置**: `.oryxos/memory/MEMORY.md`

**格式**: Markdown 文件

```markdown
## 核心记忆

### 2026-07-25
- 用户偏好：使用 Spring Boot 而非 Spring MVC
- 项目背景：OryxOS 是企业级 Agent OS

### 2026-07-24
- 关键决策：采用 SQLite 作为核心阶段持久化
```

**规则**:
- 追加写入，不修改历史记录
- 超过 4000 字时截断早期内容
- 检索支持：关键词包含匹配、正则表达式匹配、向量语义检索（扩展阶段）

---

## 9. 实体关系图

```text
Session (1) ──────< ToolInvocation (N)
    │
    └──────< LlmCall (N)

ScheduledTask (1) ──────< TaskExecution (N)

Session ────── AgentScheduler (调度触发)

Profile (YAML) ──引用──> Tool, Skill, Channel, Bootstrap
```

---

## 10. 验证规则

| 实体 | 验证规则 |
|------|---------|
| Profile | name 唯一，非空；provider.name 必须已注册 |
| Session | session_id 由 channel+user_id+profile_name 联合生成 |
| ToolInvocation | session_id 必须存在；tool_name 必须已注册 |
| LlmCall | session_id 必须存在；provider 必须已注册 |
| ScheduledTask | cron 表达式必须合法；zone 必须为有效时区 |
| Memory | 文件路径必须在 sandbox allowed_paths 内 |
