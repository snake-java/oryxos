# Quickstart: OryxOS 企业级 Agent OS

**Date**: 2026-07-25
**Feature**: 001-oryxos-full-spec

## 前提条件

- JDK 21+
- Maven 3.8+
- LLM Provider API Key（DeepSeek 或其他）

---

## 1. 快速安装

```bash
# 克隆代码
git clone https://github.com/oryx-labs/oryxos.git
cd oryxos

# 编译
mvn clean package -DskipTests

# 查看帮助
java -jar oryxos-boot/target/oryxos-boot-*.jar --help
```

---

## 2. 初始化工作区

```bash
# 初始化工作区
java -jar oryxos-boot/target/oryxos-boot-*.jar init

# 查看生成的目录结构
ls -la .oryxos/
```

**输出**:
```
.oryxos/
├── agents/
├── memory/
├── sessions/
├── profiles/
│   └── default.yaml
├── AGENTS.md
├── SOUL.md
└── USER.md
```

---

## 3. 配置 Provider

编辑 `.oryxos/profiles/default.yaml`，配置 LLM Provider：

```yaml
name: default
description: 默认助手

provider:
  name: deepseek
  model: deepseek-chat
  api_key: ${DEEPSEEK_API_KEY}  # 从环境变量读取

tools:
  - read_file
  - write_file
  - list_dir
  - shell
  - http_get
  - http_post
  - save_memory
  - recall_memory
  - notify

settings:
  max_iterations: 10
  max_history_turns: 20
```

**设置环境变量**:
```bash
export DEEPSEEK_API_KEY=your-api-key-here
```

---

## 4. 启动交互式对话

```bash
# 启动 CLI 对话
java -jar oryxos-boot/target/oryxos-boot-*.jar chat

# 或指定 Profile
java -jar oryxos-boot/target/oryxos-boot-*.jar chat --profile default
```

**示例对话**:
```
> oryxos chat
OryxOS> 你好，帮我读取 /tmp/test.txt 文件
[调用 read_file 工具]
文件内容: Hello, OryxOS!

OryxOS> 记住，我喜欢用 Spring Boot
[调用 save_memory 工具]
已保存到长期记忆

OryxOS> 退出
bye!
```

---

## 5. REST API 调用

### 5.1 启动 API 服务

```bash
# 启动 HTTP API 服务（默认端口 8080）
java -jar oryxos-boot/target/oryxos-boot-*.jar serve
```

### 5.2 创建会话

```bash
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "profile_name": "default",
    "channel": "http",
    "user_id": "user1"
  }'
```

**响应**:
```json
{
  "session_id": "http-user1-default-20260725",
  "status": "active",
  "created_at": "2026-07-25T10:00:00Z"
}
```

### 5.3 发送消息

```bash
curl -X POST http://localhost:8080/api/v1/sessions/http-user1-default-20260725/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "帮我查下天气",
    "role": "user"
  }'
```

### 5.4 查询会话历史

```bash
curl http://localhost:8080/api/v1/sessions/http-user1-default-20260725
```

### 5.5 健康检查

```bash
curl http://localhost:8080/api/v1/health
```

**响应**:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "llm": {"status": "UP"}
  }
}
```

---

## 6. Profile 管理

```bash
# 列出所有 Profile
java -jar oryxos-boot/target/oryxos-boot-*.jar profile list

# 创建新 Profile
java -jar oryxos-boot/target/oryxos-boot-*.jar profile create myagent

# 查看 Profile 详情
java -jar oryxos-boot/target/oryxos-boot-*.jar profile show myagent

# 删除 Profile
java -jar oryxos-boot/target/oryxos-boot-*.jar profile delete myagent
```

---

## 7. 定时任务配置

在 Profile 中添加 `schedules` 配置：

```yaml
name: daily-weather
description: 每日天气推送

# ... 其他配置 ...

schedules:
  - cron: "0 7 * * *"
    timezone: "Asia/Shanghai"
    message: "查询今天北京的天气，并推送穿搭建议"
```

---

## 8. 常用命令

| 命令 | 说明 |
|------|------|
| `oryxos init` | 初始化工作区 |
| `oryxos status` | 查看配置和运行状态 |
| `oryxos chat [--profile <name>]` | 交互对话 |
| `oryxos serve` | 启动 HTTP API 服务 |
| `oryxos gateway` | 启动多渠道守护进程 |
| `oryxos profile list` | 列出所有 Profile |
| `oryxos profile create <name>` | 创建新 Profile |
| `oryxos profile show <name>` | 查看 Profile 详情 |
| `oryxos profile delete <name>` | 删除 Profile |
| `oryxos provider list` | 列出已配置的 Provider |
| `oryxos tool list` | 列出已注册的 Tool |
| `oryxos session list` | 列出会话历史 |

---

## 9. 故障排除

### 9.1 LLM 调用失败

```
Error: LLM_PROVIDER_ERROR - Failed to connect to provider
```

**检查**:
- API Key 是否正确设置
- 网络是否能访问 LLM 服务商

### 9.2 Tool 调用被拒绝

```
Error: SANDBOX_VIOLATION - Path not in whitelist: /etc/passwd
```

**解决**: 在 Profile 的 `sandbox.allowed_paths` 中添加允许的路径

### 9.3 Session 创建失败

```
Error: INVALID_REQUEST - profile_name 'xxx' not found
```

**解决**: 确保 Profile 已创建且名称正确

---

## 10. 下一步

- 阅读 [AGENTS.md](../.oryxos/AGENTS.md) 了解 Agent 行为配置
- 阅读 [SOUL.md](../.oryxos/SOUL.md) 了解 Agent 人格定义
- 阅读 [USER.md](../.oryxos/USER.md) 配置用户偏好
- 查看 [docs/TechnicalSolution.md](../../docs/TechnicalSolution.md) 了解技术架构
