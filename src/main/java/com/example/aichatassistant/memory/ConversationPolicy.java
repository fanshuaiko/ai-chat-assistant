package com.example.aichatassistant.memory;

public final class ConversationPolicy {
    private ConversationPolicy() {}

    public static void validate(String message, String conversationId) {
        validateMessage(message);
        if (conversationId == null || conversationId.isBlank() || conversationId.length() > 100) {
            throw new IllegalArgumentException("conversationId 必须为 1 到 100 个字符");
        }
    }

    public static void validateMessage(String message) {
        if (message == null || message.isBlank() || message.length() > 4000) {
            throw new IllegalArgumentException("message 必须为 1 到 4000 个字符");
        }
    }
}
