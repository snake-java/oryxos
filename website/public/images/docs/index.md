# OryxOS 文档

OryxOS 是基于 Java 21 + Spring Boot 3.x 的企业级 Agent OS。

## 核心特性

- **ReAct 循环**：自实现，不依赖 Spring AI 自动执行
- **多 Provider**：DeepSeek、Qwen、Kimi、Claude 等
- **三层记忆**：会话、长期、情景记忆
- **Plugin Tool**：零代码/轻代码/重代码三档扩展
- **Web Service**：完整 REST API
- **沙箱安全**：应用层白名单校验

## 快速开始

```bash
# 构建
mvn clean package

# 启动服务
java -jar oryxos-boot/target/oryxos-boot-*.jar serve

# 交互对话
java -jar oryxos-boot/target/oryxos-boot-*.jar chat
```
