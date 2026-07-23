# 架构设计

## 整体架构

OryxOS 采用四层架构设计：

1. **接入层** - CLI Channel、Web Service、AgentScheduler
2. **Agent 层** - AgentService、ReActLoop、PromptBuilder、ToolExecutor
3. **能力层** - Provider、Memory、Tool、Notify、Sandbox
4. **基础层** - Profile、Context、Session、Config、Storage、AgentLoader

![架构图](../images/architecture.svg)
