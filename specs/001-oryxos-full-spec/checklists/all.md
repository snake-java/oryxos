# Specification Quality Checklist: OryxOS 综合检查

**Purpose**: 全面检查 OryxOS 规格说明书的需求质量
**Created**: 2026-07-25
**Feature**: [spec.md](../spec.md)

## Requirement Completeness

- [ ] CHK001 - 五大核心能力（LLM、ReAct、Memory、Tool、Web Service）是否都有明确的功能需求覆盖？ [Completeness, Spec §Requirements]
- [ ] CHK002 - 是否有明确的工作区初始化需求（.oryxos/ 目录结构）？ [Completeness, Spec §FR-001]
- [ ] CHK003 - Profile 配置的所有字段是否都有功能需求？ [Completeness, Spec §FR-002]
- [ ] CHK004 - 三种运行模式（chat/serve/gateway）是否都有明确需求？ [Completeness, Spec §FR-017]
- [ ] CHK005 - CLI 12 个子命令是否都有功能需求定义？ [Completeness, Spec §5.11]
- [ ] CHK006 - Bootstrap 文件机制是否在需求中明确？ [Completeness, Spec §FR-014]
- [ ] CHK007 - Session 状态转换规则是否在需求中定义？ [Completeness, Clarification §Q2]
- [ ] CHK008 - 可观测性指标（结构化日志、Prometheus、/health）是否在需求中定义？ [Completeness, Clarification §Q3]

## Requirement Clarity

- [ ] CHK009 - ReAct 循环的最大迭代次数是否在需求中量化？ [Clarity, Spec §FR-004]
- [ ] CHK010 - Tool 白名单校验的具体行为是否清晰定义？ [Clarity, Spec §FR-009]
- [ ] CHK011 - "定时任务到点触发成功率 ≥ 99%"是否是可验证的量化指标？ [Measurability, Spec §SC-006]
- [ ] CHK012 - Session 创建 P99 延迟 ≤ 200ms 是否明确了测量方法？ [Measurability, Spec §SC-004]
- [ ] CHK013 - "用户可以在 5 分钟内完成首个 Agent 对话"是否界定了操作步骤？ [Clarity, Spec §SC-001]

## Requirement Consistency

- [ ] CHK014 - FR-007 说"9 个内置 Tool"，但实际列出的是 9 个还是更多？ [Consistency, Spec §FR-007]
- [ ] CHK015 - Edge Cases 中说"Tool 调用超时时（默认 30 秒）"，与 Clarification Q4 的重试策略是否一致？ [Consistency, Edge Cases + Q4]
- [ ] CHK016 - SC-009/SC-010/SC-011 可观测性要求是否与 Constitution 的审计要求一致？ [Consistency, Constitution §七]

## Acceptance Criteria Quality

- [ ] CHK017 - SC-002 "单节点支持至少 10 个 Agent 并存"是否有验证方法？ [Measurability, Spec §SC-002]
- [ ] CHK018 - SC-003 "单节点支持至少 100 个并发 Session"是否定义了测试场景？ [Measurability, Spec §SC-003]
- [ ] CHK019 - SC-005 "工具调用通过白名单校验，拒绝率符合预期"是否有明确的期望值？ [Ambiguity, Spec §SC-005]
- [ ] CHK020 - 是否所有 Success Criteria 都有对应的 Acceptance Scenarios？ [Traceability, Spec §User Stories]

## Scenario Coverage

- [ ] CHK021 - 是否覆盖了用户取消会话的场景？ [Coverage, Edge Case]
- [ ] CHK022 - 是否覆盖了 LLM Provider 超时场景？ [Coverage, Edge Case]
- [ ] CHK023 - 是否覆盖了 Tool 执行结果超出 LLM context window 的场景？ [Coverage, Edge Case]
- [ ] CHK024 - 是否覆盖了多个 Agent 同时调用同一个 Tool 的并发场景？ [Coverage, Scenario §US5]
- [ ] CHK025 - 是否覆盖了 Memory 文件损坏或无法读取的场景？ [Coverage, Exception Flow]
- [ ] CHK026 - 是否覆盖了 Profile 配置文件语法错误的场景？ [Coverage, Exception Flow]
- [ ] CHK027 - 是否覆盖了 MCP Server 连接失败的场景？ [Coverage, Exception Flow]

## Edge Case Coverage

- [ ] CHK028 - 当 Memory 文件超过 4000 字时的截断策略是否明确定义？ [Edge Case, Spec §FR-006]
- [ ] CHK029 - 当达到最大 ReAct 迭代次数时，强制结束的行为是否明确定义？ [Edge Case, Spec §FR-004]
- [ ] CHK030 - 当 Tool 调用被白名单拒绝时，Agent 的替代行为是否定义？ [Edge Case, Spec §Edge Cases]
- [ ] CHK031 - 当用户发送空消息时的处理策略是否定义？ [Edge Case, Gap]
- [ ] CHK032 - 当 Session 超时自动归档时，是否通知用户？ [Edge Case, Clarification §Q2]

## Non-Functional Requirements

- [ ] CHK033 - 性能要求（延迟、并发）是否覆盖了所有关键路径？ [NFR, Spec §Success Criteria]
- [ ] CHK034 - 安全要求（白名单、敏感配置、环境变量）是否完整？ [NFR, Spec §FR-009, FR-016]
- [ ] CHK035 - 可运维性要求（部署、日志、监控）是否明确？ [NFR, Spec §SC-009/010/011]
- [ ] CHK036 - 兼容性要求（JDK 21、操作系统）是否明确定义？ [NFR, Spec §Assumptions]

## Dependencies & Assumptions

- [ ] CHK037 - 假设核心阶段使用 SQLite，H2 作为备选，这个决策是否在架构设计中体现？ [Assumption, Spec §假设存储]
- [ ] CHK038 - 假设核心阶段只内置 CLI，企业微信/飞书/钉钉放扩展阶段，Profile 配置是否考虑了 Channel 扩展？ [Assumption, Spec §假设 Channel]
- [ ] CHK039 - 依赖 Spring AI Alibaba Provider 的决策是否在技术方案中有依据？ [Dependency, Spec §假设 Provider]

## Architecture & Data Model

- [ ] CHK040 - 模块结构（oryxos-core/provider/memory/tool/...）是否与功能需求对应？ [Completeness, Constitution §模块结构]
- [ ] CHK041 - Sandbox 接口是否先于 WhitelistSandbox 实现定义？ [Architecture, Constitution §六]
- [ ] CHK042 - MemoryService 统一门面是否在架构中明确定义？ [Architecture, Constitution §十]
- [ ] CHK043 - Provider 显式映射（Map<String, ChatModel>）是否在架构中定义？ [Architecture, Constitution §五]

## API & Integration

- [ ] CHK044 - 10 个 REST API 端点是否都有明确的请求/响应格式定义？ [API, Spec §FR-011]
- [ ] CHK045 - API 错误响应格式是否统一？ [API, Gap]
- [ ] CHK046 - Plugin Tool 三档接入的接口契约是否明确？ [Integration, Spec §FR-008]
- [ ] CHK047 - MCP Client 集成是否定义了协议版本和交互模式？ [Integration, Gap]

## Traceability

- [ ] CHK048 - 功能需求（FR-001 到 FR-017）是否都能追溯到用户场景？ [Traceability, Gap]
- [ ] CHK049 - 成功标准（SC-001 到 SC-011）是否都有对应的验收场景？ [Traceability, Gap]
- [ ] CHK050 - Constitution 的 10 条原则是否都在规格说明书中有对应体现？ [Traceability, Constitution]

## Ambiguities & Conflicts

- [ ] CHK051 - "notify" Tool 在 FR-007 中列出，但其参数和返回值是否定义？ [Ambiguity, Spec §FR-007]
- [ ] CHK052 - AgentScheduler 的 cron 表达式解析器是否指定？ [Ambiguity, Gap]
- [ ] CHK053 - Profile 的 bootstrap 字段与 Bootstrap 文件（AGENTS.md/SOUL.md/USER.md）的关系是否明确？ [Ambiguity, Gap]
- [ ] CHK054 - Skill 定义（SKILL.md）的格式和加载机制是否在规格说明书中定义？ [Gap, Spec §FR-008]
- [ ] CHK055 - Profile YAML 中的 ${ENV_VAR} 占位符解析是否在需求中定义？ [Gap, Spec §假设配置]
