package com.example.aichatassistant.agent;

import com.example.aichatassistant.tool.LogisticsTool;
import com.example.aichatassistant.tool.OrderTool;
import com.example.aichatassistant.tool.ProductTool;
import org.springframework.stereotype.Component;

import java.util.List;

/** Central allowlist of tools that the model is permitted to call. */
@Component
public class ToolRegistry {
    private final List<Object> tools;

    public ToolRegistry(OrderTool orderTool, ProductTool productTool, LogisticsTool logisticsTool) {
        this.tools = List.of(orderTool, productTool, logisticsTool);
    }

    public List<Object> tools() {
        return tools;
    }
}
