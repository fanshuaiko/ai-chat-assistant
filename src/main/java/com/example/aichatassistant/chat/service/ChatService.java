package com.example.aichatassistant.chat.service;

import com.example.aichatassistant.agent.AgentService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/** Chat use cases exposed to the HTTP controller; agent behavior lives in AgentService. */
@Service
public class ChatService {
    private final AgentService agentService;

    public ChatService(AgentService agentService) {
        this.agentService = agentService;
    }

    public String chat(String message, String conversationId) {
        return agentService.respond(message, conversationId);
    }

    public String chat(String message) {
        return agentService.respondWithoutMemory(message);
    }

    public Flux<String> streamingChat(String message) {
        return agentService.streamWithoutMemory(message);
    }

    public Flux<String> chatWithSystem(String message, String conversationId) {
        return agentService.stream(message, conversationId);
    }
}
