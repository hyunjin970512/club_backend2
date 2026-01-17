package kr.co.koreazinc.data.types.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringExpression;

import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StringOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.ne(value);
        }
    },
    GREATER(">") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.gt(value);
        }
    },
    GREATER_OR_EQUAL(">=") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.goe(value);
        }
    },
    LESS("<") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.lt(value);
        }
    },
    LESS_OR_EQUAL("<=") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.loe(value);
        }
    },
    CONTAINS("contains") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.like("%" + value + "%");
        }
    },
    NOT_CONTAINS("notcontains") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.like("%" + value + "%").not();
        }
    },
    STARTS_WITH("startswith") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.startsWithIgnoreCase(value);
        }
    },
    ENDS_WITH("endswith") {

        @Override
        public BooleanExpression apply(StringExpression filed, String value) {
            return filed.endsWithIgnoreCase(value);
        }
    },
    ;

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof StringExpression expression) {
            return apply(expression, value.asText());
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(StringExpression filed, String value);

    public static StringOperator from(String symbol) {
        for (StringOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}
