package com.example.aichatassistant.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ProductTool {
    @Tool(description = "按商品名称查询演示商品的价格和库存。仅支持商品目录查询，不提供下单或修改操作。")
    public String getProduct(@ToolParam(description = "商品名称，例如 iPhone 17、MacBook Pro 或 AirPods Pro") String productName) {
        if (productName == null || productName.isBlank() || productName.length() > 80) {
            throw new IllegalArgumentException("商品名称必须为 1 到 80 个字符");
        }
        return switch (productName.trim().toLowerCase(Locale.ROOT)) {
            case "iphone 17" -> "iPhone 17：演示价格 5999 元，库存 12 件";
            case "macbook pro" -> "MacBook Pro：演示价格 16999 元，库存 5 件";
            case "airpods pro" -> "AirPods Pro：演示价格 1899 元，库存 20 件";
            default -> "没有找到商品：" + productName.trim();
        };
    }
}
