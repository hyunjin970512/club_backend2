package kr.co.koreazinc.data.types.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EnumExpression;

import kr.co.koreazinc.data.types.FilterExpression;
import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EnumOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            return filed.ne(value);
        }
    },
    CONTAINS("contains") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            // FIXME: Remove this workaround when QueryDSL supports Enum.contains
            return filed.eq(value);
        }
    },
    NOT_CONTAINS("notcontains") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            // FIXME: Remove this workaround when QueryDSL supports Enum.contains
            return filed.ne(value);
        }
    },
    STARTS_WITH("startswith") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            // FIXME: Remove this workaround when QueryDSL supports Enum.contains
            return filed.eq(value);
        }
    },
    ENDS_WITH("endswith") {

        @Override
        public BooleanExpression apply(EnumExpression filed, Enum value) {
            // FIXME: Remove this workaround when QueryDSL supports Enum.contains
            return filed.eq(value);
        }
    },
    ;

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof EnumExpression expression) {
            try {
                return apply(expression, Enum.valueOf(expression.getType(), value.asText()));
            } catch (IllegalArgumentException e) {
                return FilterExpression.of(false);
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(EnumExpression filed, Enum value);

    public static EnumOperator from(String symbol) {
        for (EnumOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}
