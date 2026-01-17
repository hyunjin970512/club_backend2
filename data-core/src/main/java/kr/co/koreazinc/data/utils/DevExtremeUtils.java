package kr.co.koreazinc.data.utils;

import java.util.Set;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.data.types.Sort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DevExtremeUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode parseFilter(String filter) {
        try {
            return objectMapper.readTree(filter);
        } catch (JacksonException e) {
            log.warn("Failed to parse filter: {}", filter, e);
        }
        return objectMapper.createArrayNode();
    }

    public static Set<Sort> parseSort(String sort) {
        try {
            return objectMapper.readValue(sort, new TypeReference<Set<Sort>>() {});
        } catch (JacksonException e) {
            log.warn("Failed to parse sort: {}", sort, e);
        }
        return Set.of();
    }

    public static <T> T parseCustomQueryParams(Class<T> clazz, String customQueryParams) {
        try {
            return objectMapper.readValue(customQueryParams, clazz);
        } catch (JacksonException e) {
            log.warn("Failed to parse custom query params: {}", customQueryParams, e);
        }
        return null;
    }

    public static <T> T updateCustomQueryParams(T value, String customQueryParams) throws JacksonException {
        return objectMapper.readerForUpdating(value).readValue(customQueryParams);
    }
}