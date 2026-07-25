# Tasks: OryxOS 企业级 Agent OS

**Input**: Design documents from `/specs/001-oryxos-full-spec/`

**Prerequisites**: plan.md (required), spec.md (required), data-model.md, contracts/, research.md

**Tests**: Tests NOT requested in feature specification — no test tasks generated

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Maven multi-module project initialization

- [ ] T001 Create parent POM at pom.xml with all 9 module declarations (oryxos-core, oryxos-provider, oryxos-memory, oryxos-tool, oryxos-channel-cli, oryxos-web, oryxos-storage, oryxos-cli, oryxos-boot)
- [ ] T002 [P] Create oryxos-core module structure at oryxos-core/src/main/java/com/oryxos/core/
- [ ] T003 [P] Create oryxos-provider module structure at oryxos-provider/src/main/java/com/oryxos/provider/
- [ ] T004 [P] Create oryxos-memory module structure at oryxos-memory/src/main/java/com/oryxos/memory/
- [ ] T005 [P] Create oryxos-tool module structure at oryxos-tool/src/main/java/com/oryxos/tool/
- [ ] T006 [P] Create oryxos-channel-cli module structure at oryxos-channel-cli/src/main/java/com/oryxos/channel/cli/
- [ ] T007 [P] Create oryxos-web module structure at oryxos-web/src/main/java/com/oryxos/web/
- [ ] T008 [P] Create oryxos-storage module structure at oryxos-storage/src/main/java/com/oryxos/storage/
- [ ] T009 [P] Create oryxos-cli module structure at oryxos-cli/src/main/java/com/oryxos/cli/
- [ ] T010 [P] Create oryxos-boot module structure at oryxos-boot/src/main/java/com/oryxos/boot/
- [ ] T011 Configure dependencies in parent pom.xml (Spring Boot 3.x, Spring AI Alibaba, SQLite, Picocli, SnakeYAML, Logback, Micrometer)
- [ ] T012 Create .oryxos/ directory structure at project root (agents/, memory/, sessions/, profiles/)
- [ ] T013 Create default Bootstrap files: AGENTS.md, SOUL.md, USER.md in .oryxos/

---

## Phase 2: Foundational (Core Infrastructure)

**Purpose**: Core interfaces and abstractions that ALL user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Core Domain Models

- [ ] T014 [P] [US1] Create Session entity at oryxos-storage/src/main/java/com/oryxos/storage/Session.java
- [ ] T015 [P] [US1] Create ToolInvocation entity at oryxos-storage/src/main/java/com/oryxos/storage/ToolInvocation.java
- [ ] T016 [P] [US1] Create LlmCall entity at oryxos-storage/src/main/java/com/oryxos/storage/LlmCall.java
- [ ] T017 [P] [US1] Create Profile entity at oryxos-core/src/main/java/com/oryxos/core/Profile.java
- [ ] T018 [P] [US1] Create ScheduledTask entity at oryxos-storage/src/main/java/com/oryxos/storage/ScheduledTask.java
- [ ] T019 [P] [US1] Create TaskExecution entity at oryxos-storage/src/main/java/com/oryxos/storage/TaskExecution.java

### Core Interfaces

- [ ] T020 [P] Create OryxTool interface at oryxos-core/src/main/java/com/oryxos/core/OryxTool.java
- [ ] T021 [P] Create Sandbox interface at oryxos-core/src/main/java/com/oryxos/core/Sandbox.java
- [ ] T022 [P] Create SandboxViolationException at oryxos-core/src/main/java/com/oryxos/core/SandboxViolationException.java

### Storage Layer

- [ ] T023 [P] Create SessionRepository at oryxos-storage/src/main/java/com/oryxos/storage/SessionRepository.java
- [ ] T024 [P] Create ToolInvocationRepository at oryxos-storage/src/main/java/com/oryxos/storage/ToolInvocationRepository.java
- [ ] T025 [P] Create LlmCallRepository at oryxos-storage/src/main/java/com/oryxos/storage/LlmCallRepository.java
- [ ] T026 [P] Create ScheduledTaskRepository at oryxos-storage/src/main/java/com/oryxos/storage/ScheduledTaskRepository.java
- [ ] T027 Configure SQLite database in oryxos-storage with schema.sql

### Provider Layer

- [ ] T028 [P] Create ProviderService at oryxos-provider/src/main/java/com/oryxos/provider/ProviderService.java
- [ ] T029 [P] Create ChatModelRegistry at oryxos-provider/src/main/java/com/oryxos/provider/ChatModelRegistry.java

### Memory Layer

- [ ] T030 [P] Create MemoryService interface at oryxos-memory/src/main/java/com/oryxos/memory/MemoryService.java
- [ ] T031 [P] Create LongTermMemory at oryxos-memory/src/main/java/com/oryxos/memory/LongTermMemory.java

### Tool Layer

- [ ] T032 [P] Create ToolRegistry at oryxos-tool/src/main/java/com/oryxos/tool/ToolRegistry.java
- [ ] T033 [P] Create WhitelistSandbox at oryxos-tool/src/main/java/com/oryxos/tool/WhitelistSandbox.java

### Core Services

- [ ] T034 Create AgentService at oryxos-core/src/main/java/com/oryxos/core/AgentService.java
- [ ] T035 Create ReActLoop at oryxos-core/src/main/java/com/oryxos/core/ReActLoop.java
- [ ] T036 Create PromptBuilder at oryxos-core/src/main/java/com/oryxos/core/PromptBuilder.java
- [ ] T037 Create ToolExecutor at oryxos-core/src/main/java/com/oryxos/core/ToolExecutor.java
- [ ] T038 Create ContextLoader at oryxos-core/src/main/java/com/oryxos/core/ContextLoader.java
- [ ] T039 Create AgentLoader at oryxos-core/src/main/java/com/oryxos/core/AgentLoader.java
- [ ] T040 Create AgentScheduler at oryxos-core/src/main/java/com/oryxos/core/AgentScheduler.java

### Configuration

- [ ] T041 Create ConfigLoader at oryxos-cli/src/main/java/com/oryxos/cli/ConfigLoader.java
- [ ] T042 Create ProfileConfigLoader at oryxos-core/src/main/java/com/oryxos/core/ProfileConfigLoader.java

### Logging & Observability

- [ ] T043 [P] Configure Logback with JSON encoder and trace ID MDC at oryxos-boot/src/main/resources/logback-spring.xml
- [ ] T044 [P] Configure Micrometer + Prometheus at oryxos-boot/src/main/resources/application.yml

**Checkpoint**: Foundation ready — user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 企业运维助手 (Priority: P1) 🎯 MVP

**Goal**: 运维助手能自动处理告警、调用工具、发送通知

**Independent Test**: 模拟 webhook 触发 Agent，验证完整 ReAct 循环和通知推送

### Built-in Tools Implementation

- [ ] T045 [P] [US1] Implement read_file tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/ReadFileTool.java
- [ ] T046 [P] [US1] Implement write_file tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/WriteFileTool.java
- [ ] T047 [P] [US1] Implement list_dir tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/ListDirTool.java
- [ ] T048 [P] [US1] Implement shell tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/ShellTool.java
- [ ] T049 [P] [US1] Implement http_get tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/HttpGetTool.java
- [ ] T050 [P] [US1] Implement http_post tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/HttpPostTool.java
- [ ] T051 [P] [US1] Implement save_memory tool at oryxos-memory/src/main/java/com/oryxos/memory/MemoryTools.java
- [ ] T052 [P] [US1] Implement recall_memory tool at oryxos-memory/src/main/java/com/oryxos/memory/MemoryTools.java
- [ ] T053 [P] [US1] Implement notify tool at oryxos-tool/src/main/java/com/oryxos/tool/tools/NotifyTool.java

### ReAct Loop Integration

- [ ] T054 [US1] Integrate all built-in tools into ToolRegistry in oryxos-tool
- [ ] T055 [US1] Implement ReActLoop message parsing for tool_calls at oryxos-core
- [ ] T056 [US1] Implement ReActLoop tool result appending to conversation history
- [ ] T057 [US1] Implement max_iterations enforcement (default 10) in ReActLoop

### CLI Channel

- [ ] T058 [P] [US1] Create CliChannel at oryxos-channel-cli/src/main/java/com/oryxos/channel/cli/CliChannel.java
- [ ] T059 [US1] Implement oryxos chat command with Picocli at oryxos-cli/src/main/java/com/oryxos/cli/OryxosCommand.java
- [ ] T060 [US1] Implement interactive multi-turn dialogue loop in CliChannel

### Audit Logging

- [ ] T061 [US1] Implement LLM call audit logging in ProviderService
- [ ] T062 [US1] Implement Tool call audit logging in ToolExecutor

**Checkpoint**: User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 知识管理助手 (Priority: P1)

**Goal**: Agent 能检索 Memory 并返回带引用来源的答案

**Independent Test**: 查询历史合同条款，验证 Memory 检索和引用追溯

### Memory Enhancement

- [ ] T063 [P] [US2] Implement keyword search in LongTermMemory (case-insensitive contains)
- [ ] T064 [P] [US2] Implement regex search in LongTermMemory
- [ ] T065 [US2] Implement MEMORY.md file size limit (4000 chars) with truncation
- [ ] T066 [US2] Implement Memory injection into PromptBuilder system prompt

**Checkpoint**: User Story 2 should be functional (reuses US1 infrastructure)

---

## Phase 5: User Story 3 - 销售助手 (Priority: P1)

**Goal**: Agent 能调用 MCP 工具获取外部数据

**Independent Test**: 模拟销售查询，验证 MCP 工具调用和多数据源聚合

### MCP Integration

- [ ] T067 [P] [US3] Create McpClientService at oryxos-tool/src/main/java/com/oryxos/tool/McpClientService.java
- [ ] T068 [P] [US3] Create McpToolAdapter at oryxos-tool/src/main/java/com/oryxos/tool/McpToolAdapter.java
- [ ] T069 [US3] Implement MCP protocol JSON-RPC 2.0 handling in McpClientService
- [ ] T070 [US3] Integrate MCP tools into ToolRegistry

### Plugin Tool Support

- [ ] T071 [P] [US3] Implement @Tool annotation scanning in oryxos-tool
- [ ] T072 [US3] Create Spring Boot auto-configuration for custom @Tool beans

**Checkpoint**: User Story 3 should be functional

---

## Phase 6: User Story 4 - 开发团队日常使用 (Priority: P2)

**Goal**: CLI 提供完整的 12 个命令

**Independent Test**: 验证所有 CLI 命令正常工作

### CLI Commands

- [ ] T073 [P] [US4] Implement oryxos init command at oryxos-cli
- [ ] T074 [P] [US4] Implement oryxos status command at oryxos-cli
- [ ] T075 [P] [US4] Implement oryxos serve command at oryxos-cli
- [ ] T076 [P] [US4] Implement oryxos gateway command at oryxos-cli
- [ ] T077 [P] [US4] Implement oryxos profile list command at oryxos-cli
- [ ] T078 [P] [US4] Implement oryxos profile create command at oryxos-cli
- [ ] T079 [P] [US4] Implement oryxos profile show command at oryxos-cli
- [ ] T080 [P] [US4] Implement oryxos profile delete command at oryxos-cli
- [ ] T081 [P] [US4] Implement oryxos provider list command at oryxos-cli
- [ ] T082 [P] [US4] Implement oryxos tool list command at oryxos-cli
- [ ] T083 [P] [US4] Implement oryxos session list command at oryxos-cli

**Checkpoint**: User Story 4 should be functional

---

## Phase 7: User Story 5 - 多 Agent 并存协作 (Priority: P2)

**Goal**: 多个 Agent 在同一实例并存运行

**Independent Test**: 同时运行多个 Agent，验证互不干扰

### Multi-Agent Support

- [ ] T084 [P] [US5] Create ProfileManager at oryxos-core/src/main/java/com/oryxos/core/ProfileManager.java
- [ ] T085 [US5] Implement Profile hot-reload in AgentService
- [ ] T086 [US5] Ensure Provider connection pooling for concurrent LLM calls
- [ ] T087 [US5] Ensure ToolExecutor thread-safety for concurrent tool calls

**Checkpoint**: User Story 5 should be functional

---

## Phase 8: User Story 6 - 定时任务自动化 (Priority: P2)

**Goal**: Agent 支持 cron 表达式定时触发

**Independent Test**: 配置定时任务，验证到点自动触发

### Scheduling

- [ ] T088 [P] [US6] Implement cron expression parsing with cron-utils in AgentScheduler
- [ ] T089 [US6] Implement ScheduledExecutorService-based task execution in AgentScheduler
- [ ] T090 [US6] Implement schedule persistence in ScheduledTaskRepository
- [ ] T091 [US6] Implement task execution logging in TaskExecution entity

**Checkpoint**: User Story 6 should be functional

---

## Phase 9: Web Service (REST API)

**Purpose**: Complete the 10 REST API endpoints

- [ ] T092 [P] Create ApiController at oryxos-web/src/main/java/com/oryxos/web/ApiController.java
- [ ] T093 [P] Create GlobalExceptionHandler at oryxos-web/src/main/java/com/oryxos/web/GlobalExceptionHandler.java
- [ ] T094 Implement POST /api/v1/sessions endpoint
- [ ] T095 Implement POST /api/v1/sessions/{id}/messages endpoint
- [ ] T096 Implement GET /api/v1/sessions/{id} endpoint
- [ ] T097 Implement DELETE /api/v1/sessions/{id} endpoint
- [ ] T098 Implement POST /api/v1/agents/{name}/invoke endpoint
- [ ] T099 Implement GET /api/v1/profiles endpoint
- [ ] T100 Implement GET /api/v1/memory endpoint
- [ ] T101 Implement GET /api/v1/tools endpoint
- [ ] T102 Implement GET /api/v1/health endpoint
- [ ] T103 Implement GET /api/v1/info endpoint

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Final integration, documentation, and cleanup

- [ ] T104 [P] Create Spring Boot auto-configuration in oryxos-boot
- [ ] T105 [P] Create main OryxOSApplication class at oryxos-boot/src/main/java/com/oryxos/boot/OryxOSApplication.java
- [ ] T106 [P] Add Prometheus metrics endpoint configuration
- [ ] T107 [P] Add OpenAPI documentation configuration
- [ ] T108 Update project README.md with quickstart instructions
- [ ] T109 Create default Profile YAML at .oryxos/profiles/default.yaml
- [ ] T110 Validate all 17 functional requirements (FR-001 to FR-017) are implemented

---

## Dependencies & Execution Order

### Phase Dependencies

| Phase | Depends On | Blocking |
|-------|-----------|----------|
| Phase 1: Setup | None | Yes |
| Phase 2: Foundational | Phase 1 | Yes |
| Phase 3-8: User Stories | Phase 2 | No |
| Phase 9: Web Service | Phase 2 | No |
| Phase 10: Polish | All prior phases | No |

### User Story Dependencies

| User Story | Depends On | Can Start After |
|------------|-----------|-----------------|
| US1 (运维助手) | Phase 2 | Phase 2 complete |
| US2 (知识管理) | Phase 2, US1 tools | Phase 3 complete |
| US3 (销售助手) | Phase 2, US1 tools | Phase 3 complete |
| US4 (CLI) | Phase 2 | Phase 2 complete |
| US5 (多Agent) | Phase 2 | Phase 2 complete |
| US6 (定时任务) | Phase 2 | Phase 2 complete |

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration

### Parallel Opportunities

- All Phase 1 module structure tasks can run in parallel (T002-T010)
- All Phase 2 entity tasks can run in parallel (T014-T019)
- All Phase 2 interface tasks can run in parallel (T020-T022)
- All Phase 2 repository tasks can run in parallel (T023-T027)
- All Phase 3 tool implementations can run in parallel (T045-T053)
- All Phase 6 CLI commands can run in parallel (T073-T083)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test US1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Phase 1-2 → Foundation ready
2. Add US1 → Test independently → Deploy/Demo (MVP!)
3. Add US2 → Test independently → Deploy/Demo
4. Add US3 → Test independently → Deploy/Demo
5. Continue with US4, US5, US6, Web Service, Polish

### Parallel Team Strategy

With multiple developers:
- One developer: Complete Phase 1-2
- Once Foundational is done:
  - Developer A: US1 (运维助手)
  - Developer B: US2 (知识管理) + US3 (销售助手)
  - Developer C: US4 (CLI) + US6 (定时任务)
  - Developer D: Web Service + Polish

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each phase or logical group
- Stop at any checkpoint to validate story independently
