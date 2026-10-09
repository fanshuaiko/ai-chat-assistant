package com.example.aichatassistant.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class LogisticsTool {
    @Tool(description = "按订单号查询物流进度。应在确认订单存在后，用订单号查询物流。只读查询。")
    public String getLogistics(@ToolParam(description = "已确认的 5 到 20 位数字订单号") String orderId) {
        String id = OrderTool.validateId(orderId, "订单号");
        return switch (id) {
            case "10001" -> "订单 10001：包裹已到达上海转运中心，预计明天送达";
            case "10002" -> "订单 10002：尚未发货，当前无物流轨迹";
            case "10003" -> "订单 10003：已于昨日签收";
            default -> "没有找到订单 " + id + " 的物流信息";
        };
    }
}
