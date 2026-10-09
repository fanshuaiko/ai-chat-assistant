package com.example.aichatassistant.chat.controller;

import com.example.aichatassistant.chat.model.ChatRequest;
import com.example.aichatassistant.chat.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


/**
 * @author Fan
 * @since 2026/9/27 10:53
 */
@RestController()
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/{message}")
    public String chat(@PathVariable String message) {
        return chatService.chat(message);
    }

    @PostMapping("/chat")
    public String chat(
            @RequestParam String conversationId,
            @RequestBody ChatRequest request
    ) {
        return chatService.chat(
                request.message(),
                conversationId
        );
    }

    @GetMapping(value = "/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }

    @PostMapping(value = "/chat/with_system",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithSystem(@RequestBody ChatRequest request) {
        return chatService.chatWithSystem(request.message(),request.conversationId());
    }
}
