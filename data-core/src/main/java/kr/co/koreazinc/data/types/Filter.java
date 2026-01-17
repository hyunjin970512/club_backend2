package kr.co.koreazinc.data.types;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

@Getter
public class Filter {

    private String fieldName;

    private String operator;

    private JsonNode value;

    public Filter(JsonNode node) {
        if (!Filter.isSupport(node)) {
            throw new IllegalArgumentException("Invalid condition node: " + node);
        }
        this.fieldName = node.get(0).asText();
        this.operator = node.get(1).asText();
        this.value = node.get(2);
    }

    public static boolean isSupport(JsonNode node) {
        return node.isArray() && node.size() == 3 && node.get(0).isTextual() && node.get(1).isTextual();
    }
}