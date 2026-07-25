# Feature Specification: OryxOS 企业级 Agent OS

**Feature Branch**: `001-oryxos-full-spec`

**Created**: 2026-07-25

**Status**: Draft

**Input**: 用户要求为 OryxOS 完整项目创建规格说明书，基于需求文档 (DemandAnalysis.md) 和项目概述 (oryxos.md)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 企业运维助手 (Priority: P1)

某中型 SaaS 公司的运维团队使用 OryxOS 搭一个运维助手，通过企业微信接入。Agent 配了多个 Tool（告警分诊、日志查询、服务重启、变更审批）。凌晨告警通过 webhook 进 OryxOS，Agent 收到告警后调用日志查询 Tool 拉错误堆栈，跟历史故障库交叉引用发现是已知 bug，自动应用 mitigation Skill 重启服务，在企业微信运维群里汇报"已自愈，详情见附件"。

**Why this priority**: 运维助手是 OryxOS 的核心场景之一，能体现 Agent OS 的核心价值（自动执行、多工具协作、记忆、审计）。

**Independent Test**: 可以通过模拟告警 webhook 触发 Agent，验证完整的 ReAct 循环、Tool 调用、Memory 检索、通知推送全链路。

**Acceptance Scenarios**:

1. **Given** 凌晨收到 Prometheus 告警 webhook，**When** Agent 收到告警消息，**Then** Agent 自动调用日志查询 Tool 拉取错误堆栈
2. **Given** Agent 获取到错误堆栈，**When** Agent 检索历史故障库，**Then** 发现是已知 bug 并应用 mitigation 重启服务
3. **Given** 服务重启成功，**When** Agent 发送通知到企业微信，**Then** 运维群里收到"已自愈"消息

---

### User Story 2 - 知识管理助手 (Priority: P1)

某金融企业的法务团队使用 OryxOS 搭一个知识管理 Agent，接入飞书。Agent 索引了内部的合同模板、法规文档、历史案例、咨询记录。员工在飞书里问"上次签 SaaS 服务协议是怎么处理数据出境条款的"，Agent 检索 Memory 拉出历史案例，综合相关法规给出建议草稿，标注引用来源。

**Why this priority**: 知识管理是企业刚需，Memory 的价值在此场景充分体现，合规要求所有 Agent 回复可追溯。

**Independent Test**: 可以通过查询历史合同条款，验证 Memory 检索准确度和引用追溯能力。

**Acceptance Scenarios**:

1. **Given** 员工查询数据出境条款处理方式，**When** Agent 检索 Memory，**Then** 返回历史案例并标注引用来源
2. **Given** Agent 找到多个相关案例，**When** Agent 综合法规给出建议，**Then** 建议草稿包含完整的引用链路

---

### User Story 3 - 销售助手 (Priority: P1)

某制造业企业销售部门使用 OryxOS 搭一个客户洞察 Agent，接入企业微信和 CRM。销售跑客户前问 Agent"明天去拜访 A 公司，有什么我需要知道的"，Agent 调用 CRM connector 拉客户历史交易记录，调用企查查 MCP 工具查最新工商信息，调用知识库 Tool 提取关键决策人和采购习惯，综合输出客户简报。

**Why this priority**: 销售助手场景展示 MCP 集成能力和多工具编排能力，是企业级应用的核心形态。

**Independent Test**: 可以通过模拟销售查询，验证 MCP 工具调用、多数据源聚合能力。

**Acceptance Scenarios**:

1. **Given** 销售查询 A 公司客户简报，**When** Agent 调用 CRM connector，**Then** 获取历史交易记录
2. **Given** Agent 获取到交易记录，**When** Agent 调用企查查 MCP，**Then** 获取最新工商信息
3. **Given** Agent 获取到工商信息，**When** Agent 综合输出简报，**Then** 简报包含客户画像、决策人、采购习惯

---

### User Story 4 - 开发团队日常使用 (Priority: P2)

开发团队通过 CLI (`oryxos chat`) 与 Agent 对话，完成代码阅读、问题诊断、文档生成等日常任务。Agent 记住团队的开发惯例（如"我们用 Lombok 不用手写 getter/setter"），在后续对话中自动应用。

**Why this priority**: CLI 是核心阶段的主要接入方式，开发者体验直接影响项目推广。

**Independent Test**: 可以通过 CLI 对话验证多轮对话、Tool 调用、Memory 记住偏好全链路。

**Acceptance Scenarios**:

1. **Given** 开发者在 CLI 中与 Agent 对话，**When** 开发者说"我一般用 Spring Boot 不用 Spring MVC"，**Then** Agent 调用 save_memory 记住这个偏好
2. **Given** Agent 记住了开发者偏好，**When** 开发者下次问"帮我生成一个 Controller"，**Then** Agent 生成的是 Spring Boot 风格的代码

---

### User Story 5 - 多 Agent 并存协作 (Priority: P2)

企业在同一 OryxOS 实例上运行多个 Agent（运维助手、知识管理助手、销售助手），各 Agent 共享底座（Channel、Provider、Tool、Memory、Sandbox），通过 Profile 配置区分。

**Why this priority**: "多 Agent 并存"是 Agent OS 与单一 Agent 框架的核心区别，是"OS"概念的体现。

**Independent Test**: 可以同时启动多个 Agent，验证它们独立运行、互不干扰、共享底座资源。

**Acceptance Scenarios**:

1. **Given** 企业配置了三个不同 Profile 的 Agent，**When** 三个 Agent 同时运行，**Then** 各自独立处理请求，互不干扰
2. **Given** 三个 Agent 共享同一个 LLM Provider，**When** 同时调用 LLM，**Then** Provider 正确处理并发请求

---

### User Story 6 - 定时任务自动化 (Priority: P2)

企业配置定时任务，让 Agent 每天早上自动执行（如"每日天气推送"、"每日科技日报"）。到点自动触发，不需要人工干预。

**Why this priority**: 定时任务是 Agent OS 的第三触发源（CLI、Web Service 之外），是自动化场景的核心能力。

**Independent Test**: 可以配置一个定时任务，验证到点自动触发、完整执行、结果推送。

**Acceptance Scenarios**:

1. **Given** 配置了"每日天气推送"定时任务（cron: "0 7 * * *"），**When** 到达设定时间，**Then** Agent 自动查询天气并推送
2. **Given** 定时任务执行完成，**When** 查看 session 记录，**Then** 能看到完整的自动触发对话历史

---

### Edge Cases

- 当 LLM Provider 故障时，系统必须报错而非静默失败
- 当 Tool 调用超时时（默认 30 秒），Agent 必须能够处理并告知用户
- 当 Memory 文件超过 4000 字时，系统必须做截断处理
- 当达到最大 ReAct 迭代次数（默认 10 次）时，Agent 必须强制结束并返回当前结果
- 当 Tool 调用被白名单拒绝时，Agent 必须收到明确错误信息并尝试其他方案
- 当 Tool 调用超时时，系统执行指数退避重试策略：最多 3 次（1s → 2s → 4s），单次超时 30 秒

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: 系统必须支持工作区初始化（`oryxos init`），创建 `.oryxos/` 目录及子目录结构
- **FR-002**: 系统必须支持 Profile 配置管理（CRUD），Profile 决定 Agent 的运行时宿主配置
- **FR-003**: 系统必须通过 Provider 抽象层对接主流 LLM（DeepSeek、通义、Kimi 等），Agent 不感知具体厂商
- **FR-004**: 系统必须实现 ReAct 循环（Reason + Act），支持多轮 Tool 调用直到给出最终响应
- **FR-005**: 系统必须实现会话记忆（Session），持久化到 SQLite，支持跨重启恢复
- **FR-006**: 系统必须实现长期记忆（MEMORY.md 文件），支持 save_memory 和 recall_memory 两个内置 Tool；recall_memory 支持三种检索模式：关键词包含匹配（不区分大小写）、正则表达式匹配、扩展阶段向量语义检索
- **FR-007**: 系统必须内置 9 个 Tool：read_file、write_file、list_dir、shell、http_get、http_post、save_memory、recall_memory、notify
- **FR-008**: 系统必须支持 Plugin Tool 三档接入（SKILL.md + MCP、MCP server、@Tool 注解）
- **FR-009**: 系统必须通过 Sandbox 接口对 Tool 调用进行白名单校验（路径、命令、域名）
- **FR-010**: 系统必须提供 CLI Channel（`oryxos chat`），支持交互式多轮对话
- **FR-011**: 系统必须提供 Web Service，暴露 10 个 REST API 端点
- **FR-012**: 系统必须实现定时任务调度（AgentScheduler），支持 cron 表达式配置
- **FR-013**: 系统必须记录所有 LLM 调用和 Tool 调用到 audit 日志（tool_invocations、llm_calls）
- **FR-014**: 系统必须支持 Bootstrap 文件（AGENTS.md、SOUL.md、USER.md）加载到系统提示词
- **FR-015**: 系统必须支持多 Agent 并存，通过 Profile 配置区分，各 Agent 共享底座资源
- **FR-016**: 系统必须支持环境变量注入敏感配置（API key、token），禁止明文写入配置文件
- **FR-017**: 系统必须支持三种运行模式：交互对话（chat）、HTTP API（serve）、守护进程（gateway）

### Key Entities

- **Profile**: Agent 的运行时宿主配置，包含 name、provider、tools、skills、channels、settings
- **Session**: 用户和 Agent 一次对话的上下文容器，包含对话历史、状态（active/archived）、时间戳；状态转换：active → archived 通过用户主动调用 DELETE API 或超时自动归档（默认 24 小时无活动）
- **Provider**: LLM API 服务的抽象，实现统一接口让 Agent 不感知具体厂商
- **Tool**: Agent 可以调用的外部能力，分内置 Tool 和 Plugin Tool
- **Memory**: Agent 跨对话保留的状态，分会话记忆和长期记忆（MEMORY.md）
- **Channel**: Agent 对外接入的消息入口，核心阶段支持 CLI
- **AgentScheduler**: 定时任务调度器，支持 cron 表达式触发 Agent

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 用户可以在 5 分钟内通过 `oryxos init` 和 `oryxos chat` 完成首个 Agent 对话
- **SC-002**: 单节点支持至少 10 个 Agent 并存运行
- **SC-003**: 单节点支持至少 100 个并发 Session
- **SC-004**: Session 创建 P99 延迟 ≤ 200ms（不含 LLM 调用本身延迟）
- **SC-005**: 工具调用通过白名单校验，拒绝率符合预期（恶意调用被拦截）
- **SC-006**: 定时任务到点触发成功率 ≥ 99%（在 Provider 正常的前提下）
- **SC-007**: 用户可以通过 REST API 在 5 分钟内完成会话创建、消息发送、历史查询全流程
- **SC-008**: 系统支持通过环境变量注入敏感配置，配置加载时做基础校验并给出清晰报错
- **SC-009**: 系统必须输出结构化 JSON 日志，包含 trace ID串联请求
- **SC-010**: 系统必须暴露 Prometheus 指标（LLM 调用次数、Tool 调用次数、Session 数量、延迟分布）
- **SC-011**: 系统必须提供 /health 健康检查端点

## Assumptions

- **假设目标用户**：企业后端开发团队、运维团队、业务部门负责人
- **假设部署环境**：企业自己的 K8s、虚拟机或物理机，数据不出域
- **假设网络环境**：内网环境，API 调用支持 HTTPS
- **假设 Provider**：核心阶段先跑通 DeepSeek 或 Kimi，其他 Provider 扩展阶段补齐
- **假设存储**：核心阶段使用 SQLite 持久化，H2 作为备选（技术方案阶段决策）
- **假设启动时间**：Java 应用启动慢是可接受的，GraalVM Native Image 放扩展阶段
- **假设安全**：核心阶段使用应用层白名单，不使用 SecurityManager（JDK 17 已废弃）
- **假设 Channel**：核心阶段只内置 CLI，企业微信/飞书/钉钉放扩展阶段
- **假设核心阶段范围**：核心阶段包含全部 17 条功能需求（FR-001 至 FR-017），4 周/12 小时的约束通过每周聚焦不同能力线来管理

## Clarifications

### Session 2026-07-25

- Q: 核心阶段的明确范围边界 → A: 核心阶段包含全部 17 条功能需求
- Q: Session 何时从 active 转为 archived → A: 用户主动归档（DELETE API）或超时自动归档（默认 24 小时无活动）
- Q: 系统需要暴露哪些可观测性指标 → A: 结构化 JSON 日志（包含 trace ID 串联请求）+ Prometheus 指标（LLM/Tool 调用次数、Session 数量、延迟分布）+ /health 健康检查端点
- Q: Tool 调用失败时应该如何重试 → A: 指数退避最多 3 次（1s → 2s → 4s），单次超时 30 秒
- Q: Memory 检索使用什么匹配策略 → A: 三种模式：关键词包含匹配（不区分大小写）、正则表达式匹配、扩展阶段向量语义检索
