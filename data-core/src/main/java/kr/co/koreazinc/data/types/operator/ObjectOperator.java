package kr.co.koreazinc.data.types.operator;

import java.util.UUID;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

import kr.co.koreazinc.data.types.FilterExpression;
import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ObjectOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(ComparableExpression filed, Object value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(ComparableExpression filed, Object value) {
            return filed.ne(value);
        }
    },
    ;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof ComparableExpression expression) {
            if (expression.getType().isAssignableFrom(UUID.class)) {
                return apply(expression, UUID.fromString(value.asText()));
            }
            // TODO: Handle other primitive types if needed
            try {
                return apply(expression, objectMapper.readValue(value.asText(), expression.getType()));
            } catch (JacksonException e) {
                return FilterExpression.of(false);
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(ComparableExpression filed, Object value);

    public static ObjectOperator from(String symbol) {
        for (ObjectOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}
