package com.common_logging.logging.library.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class LogMaskingUtils {

    public static String maskJsonFields(String json, List<String> fieldsToMask, ObjectMapper objectMapper) {
        if (json == null || json.isBlank() || fieldsToMask == null || fieldsToMask.isEmpty()) return json;
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isObject()) {
                maskNode((ObjectNode) node, fieldsToMask.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toSet()));
                return objectMapper.writeValueAsString(node);
            } else {
                return json;
            }
        } catch (Exception ex) {
            // not JSON or parse error: return original
            return json;
        }
    }

    private static void maskNode(ObjectNode node, Set<String> lowerCaseFieldsToMask) {
        node.fieldNames().forEachRemaining(field -> {
            JsonNode value = node.get(field);
            if (lowerCaseFieldsToMask.contains(field.toLowerCase(Locale.ROOT))) {
                node.put(field, "****");
            } else if (value.isObject()) {
                maskNode((ObjectNode) value, lowerCaseFieldsToMask);
            }
        });
    }

    public static String truncate(String s, int max) {
        if (s == null) return null;
        if (max <= 0) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...(truncated)";
    }
}
