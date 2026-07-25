# API Contracts: OryxOS REST API

**Date**: 2026-07-25
**Feature**: 001-oryxos-full-spec
**Base URL**: `/api/v1`

---

## 1. 会话管理

### 1.1 创建会话

**POST** `/sessions`

**Request Body**:
```json
{
  "profile_name": "string",
  "channel": "cli|http|webhook",
  "user_id": "string"
}
```

**Response** (201 Created):
```json
{
  "session_id": "cli-user-admin-20260725",
  "profile_name": "admin",
  "channel": "cli",
  "user_id": "user",
  "status": "active",
  "created_at": "2026-07-25T10:00:00Z"
}
```

---

### 1.2 发送消息

**POST** `/sessions/{id}/messages`

**Path Parameters**:
- `id`: Session ID

**Request Body**:
```json
{
  "content": "string",
  "role": "user"
}
```

**Response** (200 OK):
```json
{
  "session_id": "cli-user-admin-20260725",
  "messages": [
    {
      "role": "user",
      "content": "帮我查下天气",
      "created_at": "2026-07-25T10:00:00Z"
    },
    {
      "role": "assistant",
      "content": "今天北京晴，温度 25-32 度",
      "created_at": "2026-07-25T10:00:01Z",
      "tool_calls": []
    }
  ]
}
```

**Error Responses**:
- 404: Session not found
- 400: Invalid request body

---

### 1.3 查询会话历史

**GET** `/sessions/{id}`

**Path Parameters**:
- `id`: Session ID

**Response** (200 OK):
```json
{
  "session_id": "cli-user-admin-20260725",
  "profile_name": "admin",
  "channel": "cli",
  "user_id": "user",
  "status": "active",
  "messages": [...],
  "created_at": "2026-07-25T10:00:00Z",
  "last_active_at": "2026-07-25T10:05:00Z"
}
```

---

### 1.4 归档会话

**DELETE** `/sessions/{id}`

**Path Parameters**:
- `id`: Session ID

**Response** (200 OK):
```json
{
  "session_id": "cli-user-admin-20260725",
  "status": "archived",
  "archived_at": "2026-07-25T12:00:00Z"
}
```

---

## 2. Agent 调用

### 2.1 无状态调用

**POST** `/agents/{name}/invoke`

**Path Parameters**:
- `name`: Agent/Profile 名称

**Request Body**:
```json
{
  "message": "string",
  "user_id": "string"
}
```

**Response** (200 OK):
```json
{
  "response": "string",
  "session_id": "cli-user-admin-20260725",
  "tool_invocations": [
    {
      "tool_name": "http_get",
      "input": {"url": "..."},
      "result": "...",
      "success": true,
      "duration_ms": 150
    }
  ]
}
```

---

## 3. Profile 管理

### 3.1 列出 Profile

**GET** `/profiles`

**Response** (200 OK):
```json
{
  "profiles": [
    {
      "name": "admin",
      "description": "管理员助手",
      "provider": {
        "name": "deepseek",
        "model": "deepseek-chat"
      },
      "tool_count": 9
    }
  ]
}
```

---

## 4. Memory 操作

### 4.1 查询长期记忆

**GET** `/memory`

**Query Parameters**:
- `query` (optional): 检索关键词

**Response** (200 OK):
```json
{
  "content": "## 核心记忆\n\n### 2026-07-25\n- 用户偏好：使用 Spring Boot",
  "matched_lines": [
    "## 核心记忆",
    "### 2026-07-25",
    "- 用户偏好：使用 Spring Boot"
  ]
}
```

---

## 5. Tool 信息

### 5.1 列出可用 Tool

**GET** `/tools`

**Response** (200 OK):
```json
{
  "tools": [
    {
      "name": "read_file",
      "description": "读取文件内容",
      "parameters": {
        "path": {"type": "string", "required": true}
      }
    },
    {
      "name": "http_get",
      "description": "发起 GET 请求",
      "parameters": {
        "url": {"type": "string", "required": true}
      }
    }
  ]
}
```

---

## 6. 系统状态

### 6.1 健康检查

**GET** `/health`

**Response** (200 OK):
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "llm": {"status": "UP"}
  }
}
```

### 6.2 运行信息

**GET** `/info`

**Response** (200 OK):
```json
{
  "version": "1.0.0",
  "name": "OryxOS",
  "java_version": "21",
  "providers": ["deepseek", "qwen", "kimi"],
  "active_sessions": 5,
  "uptime_seconds": 3600
}
```

---

## 7. 错误响应格式

所有错误响应遵循以下格式：

```json
{
  "error": {
    "code": "SESSION_NOT_FOUND",
    "message": "Session with id 'xxx' not found",
    "timestamp": "2026-07-25T10:00:00Z",
    "trace_id": "abc123"
  }
}
```

**错误码**:
| Code | HTTP Status | 说明 |
|------|-------------|------|
| SESSION_NOT_FOUND | 404 | Session 不存在 |
| PROFILE_NOT_FOUND | 404 | Profile 不存在 |
| INVALID_REQUEST | 400 | 请求参数无效 |
| TOOL_INVOCATION_FAILED | 500 | Tool 调用失败 |
| LLM_PROVIDER_ERROR | 502 | LLM Provider 调用失败 |
| SANDBOX_VIOLATION | 403 | 沙箱校验拒绝 |
| INTERNAL_ERROR | 500 | 内部错误 |
