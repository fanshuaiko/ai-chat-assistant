package com.example.aichatassistant.chat;

import com.example.aichatassistant.agent.AgentService;
import com.example.aichatassistant.agent.ToolRegistry;
import com.example.aichatassistant.chat.service.ChatService;
import com.example.aichatassistant.prompt.AssistantPrompt;
import com.example.aichatassistant.tool.LogisticsTool;
import com.example.aichatassistant.tool.OrderTool;
import com.example.aichatassistant.tool.ProductTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AgentAssistantTests {
    private StubChatModel model;
    private ChatMemory memory;
    private ChatService service;

    @BeforeEach
    void setUp() {
        model = new StubChatModel();
        memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository()).maxMessages(30).build();
        AgentService agent = new AgentService(ChatClient.builder(model), memory,
                new ToolRegistry(new OrderTool(), new ProductTool(), new LogisticsTool()));
        service = new ChatService(agent);
    }

    @Test
    void ordinaryQuestionUsesModelWithoutTools() {
        assertEquals("普通回答", service.chat("什么是 JVM？", "chat-a"));
        assertEquals(1, model.calls.get());
    }

    @Test
    void orderToolReturnsExpectedOrder() {
        String result = new OrderTool().getOrder("10001");
        assertTrue(result.contains("订单号：10001"));
        assertTrue(result.contains("已发货"));
    }

    @Test
    void orderThenLogisticsToolResultsCanBeCombined() {
        String order = new OrderTool().getOrder("10001");
        String logistics = new LogisticsTool().getLogistics("10001");
        assertTrue(AssistantPrompt.SYSTEM.contains("先调用订单查询确认订单"));
        assertTrue(order.contains("已发货"));
        assertTrue(logistics.contains("上海转运中心"));
    }

    @Test
    void differentConversationIdsKeepIndependentMemory() {
        service.chat("我的代号是 blue-owl", "chat-a");
        service.chat("我的代号是 red-fox", "chat-b");
        int split = model.prompts.size();
        service.chat("继续", "chat-a");
        String aPrompt = model.prompts.get(split).getContents();
        service.chat("继续", "chat-b");
        String bPrompt = model.prompts.get(split + 1).getContents();
        assertTrue(aPrompt.contains("blue-owl"));
        assertFalse(aPrompt.contains("red-fox"));
        assertTrue(bPrompt.contains("red-fox"));
        assertFalse(bPrompt.contains("blue-owl"));
    }

    @Test
    void toolsValidateInputsAndReturnOnlyDemoData() {
        assertTrue(new OrderTool().getOrder("10001").contains("已发货"));
        assertTrue(new LogisticsTool().getLogistics("10001").contains("上海转运中心"));
        assertTrue(new ProductTool().getProduct("iPhone 17").contains("5999"));
        assertThrows(IllegalArgumentException.class, () -> new OrderTool().getOrder("10001; delete"));
        assertThrows(IllegalArgumentException.class, () -> new LogisticsTool().getLogistics("../10001"));
        assertThrows(IllegalArgumentException.class, () -> new ProductTool().getProduct(" "));
    }

    private static final class StubChatModel implements ChatModel {
        private final AtomicInteger calls = new AtomicInteger();
        private final List<Prompt> prompts = new ArrayList<>();

        @Override
        public ChatResponse call(Prompt prompt) {
            prompts.add(prompt);
            calls.incrementAndGet();
            return new ChatResponse(List.of(new org.springframework.ai.chat.model.Generation(
                    new org.springframework.ai.chat.messages.AssistantMessage("普通回答"))));
        }

        private String allPromptText() {
            return prompts.stream().map(Prompt::getContents).reduce("", (a, b) -> a + "\n" + b);
        }
    }
}
