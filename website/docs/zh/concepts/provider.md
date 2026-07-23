# Provider

## 什么是 Provider

Provider 是 LLM 的抽象层，对接主流大模型（DeepSeek、Qwen、Kimi 等）。

## 核心特性

- 显式映射 `Map<String, ChatModel>`
- 不依赖类型扫描
- Agent 不感知具体调哪家模型
