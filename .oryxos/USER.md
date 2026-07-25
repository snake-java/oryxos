# USER.md - User Preferences

## About This File

This file stores user preferences and context that the Agent should remember across conversations. Update this file to personalize your experience with OryxOS.

## Default Preferences

### Language
- Primary language: Chinese (中文)
- Agent should respond in the user's preferred language

### Code Conventions
- Default: Use standard conventions for the detected language
- Update below with project-specific conventions

### Communication
- Tone: Professional and concise
- Detail level: Provide sufficient context without overwhelming

## User-Specific Notes

<!-- Add your preferences below. The Agent will load these when starting a conversation. -->

### Example Preferences

- **Preferred programming language**: Java
- **Code style**: Follow Google style guides
- **Common tasks**: Code review, documentation writing, bug investigation
- **Preferred LLM**: DeepSeek (for complex tasks), Qwen (for quick queries)
- **Timezone**: Asia/Shanghai

## Session History

Session history is maintained automatically by OryxOS. The Agent can recall previous conversations through the memory system.

## Memory Management

The Agent automatically saves important information to long-term memory using the `save_memory` tool. Key information that should be remembered:

- User's name and role
- Project background and conventions
- Preferred tools and workflows
- Important decisions and their rationale
