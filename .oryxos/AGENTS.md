# AGENTS.md - Project-Level Agent Behavior

## OryxOS Agent System

This file defines project-level behavior instructions for all Agents running on OryxOS.

### Core Principles

1. **Agent Harness OS**: OryxOS is a harness that runs Agents. The harness provides LLM routing, tool execution, memory, sandboxing, and audit capabilities.

2. **Configuration-Driven**: An Agent is defined by its Profile (YAML configuration) and Skill (SKILL.md files). Agents do not require code changes to be created or modified.

3. **Private & Secure**: All data stays within the enterprise infrastructure. No data is sent to external services without explicit tool calls.

4. **Audit Everything**: All LLM calls and tool invocations are logged for compliance and debugging.

### Agent Behavior Guidelines

- Agents should use tools efficiently - call tools only when necessary
- Agents should provide clear, actionable responses
- Agents should respect sandbox constraints and inform users when actions are blocked
- Agents should maintain context across conversation turns using memory tools
- Agents should admit uncertainty when they cannot complete a task

### ReAct Loop

Agents operate using the ReAct (Reason + Act) loop:
1. Receive user message
2. Reason about whether to call tools
3. Call tools if needed (file operations, HTTP requests, memory, etc.)
4. Observe tool results
5. Continue reasoning or provide final response

### Tool Categories

- **File Tools**: read_file, write_file, list_dir - operate on whitelisted paths
- **Shell Tools**: shell - execute whitelisted commands
- **HTTP Tools**: http_get, http_post - call whitelisted domains
- **Memory Tools**: save_memory, recall_memory - manage long-term memory
- **Notify Tools**: notify - send notifications to configured channels

### Multi-Agent Environment

Multiple Agents can run on the same OryxOS instance, each with its own Profile:
- Different Agents can use different LLM providers
- Different Agents can have different tool sets
- Agents share the same infrastructure (database, memory, sandbox)
- Agents do not share conversation history unless explicitly configured
