# Project 01 · Java AI Assistant

按 Agent Assistant 分层组织的 Java AI 应用，使用 JDK 25、Spring Boot 4.1.1、Maven、Spring AI 2.0.0 和 DeepSeek。支持普通问答、SSE 流式聊天、按 `conversationId` 隔离的会话记忆，以及只读订单、物流、商品查询工具。

## 运行

设置 DeepSeek API Key 后启动：

```bash
export DEEPSEEK_API_KEY="你的 DeepSeek API Key"
./mvnw spring-boot:run
```

浏览器访问 `http://localhost:8080` 使用现有聊天页面。未配置 Key 时应用仍可启动，但模型请求无法成功。

## 目录

```text
com.example.aichatassistant
├── chat
│   ├── controller     HTTP API 和请求错误映射
│   ├── service        面向 Controller 的聊天用例
│   └── model          请求/响应模型
├── agent
│   ├── AgentService   ChatClient 调用、工具调用和会话上下文
│   └── ToolRegistry   Agent 可调用工具的显式 allowlist
├── tool              OrderTool、ProductTool、LogisticsTool
├── memory            会话参数校验策略
├── prompt            Agent 系统提示词
└── config             ChatMemory 和 API 异常配置
```

`ChatService` 将 HTTP 层和 Agent 编排解耦；`AgentService` 组合 `ChatClient`、记忆 Advisor、提示词和 `ToolRegistry`；每个工具只负责一种受限的只读查询。数据目前是演示数据，可将工具内部实现替换为业务服务调用。

## Agent / Tool Calling 流程

1. 请求携带用户消息和 `conversationId`。记忆 Advisor 只装载同一 ID 的对话历史。
2. ChatClient 将 system prompt、用户消息和工具定义发送给 DeepSeek。
3. 模型依据工具名称、描述及参数 Schema 选择工具。询问“查询订单 10001 并告诉我物流到哪里了”时，prompt 要求先查订单，再按已确认订单号查询物流。
4. Spring AI 在服务端执行 Java 工具方法，将受限的结果回传模型；模型可以继续调用另一个工具，最后组织自然语言回答。
5. 所有工具只返回演示数据，不接受任意查询表达式，也不暴露数据库、文件系统或网络访问。订单 ID 限定为 5 到 20 位数字；商品名长度受限。

## API

- `POST /api/chat?conversationId=<id>`，JSON：`{"message":"你好"}`，返回同步文本。
- `POST /api/chat/with_system`，JSON：`{"message":"你好","conversationId":"<id>"}`，以 SSE 返回内容，前端现有页面继续使用。
- `GET /api/chat/stream?message=<url-encoded-message>`，保留旧的 SSE 接口。
- `GET /api/chat/{message}`，保留旧的简易接口（不关联会话记忆）。

消息长度限制为 4000 字符，会话 ID 长度限制为 100 字符；参数无效时返回 HTTP 400。

## 测试

```bash
./mvnw test
./mvnw verify
```

测试不访问 DeepSeek：普通问答和会话记忆使用假模型；工具单查、订单后查物流的工具结果组合以及非法输入通过单元测试验证。
