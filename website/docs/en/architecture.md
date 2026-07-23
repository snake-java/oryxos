# Architecture

## Overview

OryxOS uses a four-layer architecture:

1. **Access Layer** - CLI Channel, Web Service, AgentScheduler
2. **Agent Layer** - AgentService, ReActLoop, PromptBuilder, ToolExecutor
3. **Capability Layer** - Provider, Memory, Tool, Notify, Sandbox
4. **Foundation Layer** - Profile, Context, Session, Config, Storage, AgentLoader

![Architecture](/images/architecture.svg)
