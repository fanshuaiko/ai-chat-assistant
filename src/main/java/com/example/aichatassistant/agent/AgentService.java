package com.example.aichatassistant.agent;

import com.example.aichatassistant.memory.ConversationPolicy;
import com.example.aichatassistant.prompt.AssistantPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AgentService {
    private final ChatClient chatClient;
    private final ToolRegistry toolRegistry;

    public AgentService(ChatClient.Builder builder, ChatMemory chatMemory, ToolRegistry toolRegistry) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
        this.toolRegistry = toolRegistry;
    }

    public String respond(String message, String conversationId) {
        ConversationPolicy.validate(message, conversationId);
        return chatClient.prompt().system(AssistantPrompt.SYSTEM)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .tools(toolRegistry.tools().toArray()).user(message).call().content();
    }

    public Flux<String> stream(String message, String conversationId) {
        ConversationPolicy.validate(message, conversationId);
        return chatClient.prompt().system(AssistantPrompt.SYSTEM)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .tools(toolRegistry.tools().toArray()).user(message).stream().content();
    }

    public String respondWithoutMemory(String message) {
        ConversationPolicy.validateMessage(message);
        return chatClient.prompt().system(AssistantPrompt.SYSTEM)
                .tools(toolRegistry.tools().toArray()).user(message).call().content();
    }

    public Flux<String> streamWithoutMemory(String message) {
        ConversationPolicy.validateMessage(message);
        return chatClient.prompt().system(AssistantPrompt.SYSTEM)
                .tools(toolRegistry.tools().toArray()).user(message).stream().content();
    }
}
