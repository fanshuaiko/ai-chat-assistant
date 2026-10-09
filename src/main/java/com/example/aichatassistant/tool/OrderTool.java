package com.example.aichatassistant.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderTool {
    @Tool(description = "按订单号查询订单摘要。用户询问订单状态、订单商品，或需要确认订单后查询物流时使用。只读查询。")
    public String getOrder(@ToolParam(description = "订单号，格式为 5 到 20 位数字，例如 10001") String orderId) {
        String id = validateId(orderId, "订单号");
        return switch (id) {
            case "10001" -> "订单号：10001；商品：iPhone 17；状态：已发货";
            case "10002" -> "订单号：10002；商品：MacBook Pro；状态：待付款";
            case "10003" -> "订单号：10003；商品：AirPods Pro；状态：已完成";
            default -> "没有找到订单：" + id;
        };
    }

    static String validateId(String value, String label) {
        if (value == null || !value.matches("[0-9]{5,20}")) {
            throw new IllegalArgumentException(label + "格式无效");
        }
        return value;
    }
}
