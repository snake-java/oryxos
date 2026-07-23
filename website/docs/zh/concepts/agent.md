# Agent

## 什么是 Agent

Agent（智能体）是 OryxOS 的核心业务实体，代表一个具象的业务智能体。

## Agent 定义

一个目录 = 一个 Agent：

```
.oryxos/agents/<name>/
├── AGENT.md           # frontmatter + 正文
├── skills/           # 可选子指令
├── scripts/          # 可选脚本
└── REFERENCE.md      # 可选参考
```
