package kr.co.koreazinc.data.types.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.NumberExpression;

import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NumberOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.ne(value);
        }
    },
    GREATER(">") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.gt(value);
        }
    },
    GREATER_OR_EQUAL(">=") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.goe(value);
        }
    },
    LESS("<") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.lt(value);
        }
    },
    LESS_OR_EQUAL("<=") {

        @Override
        public BooleanExpression apply(NumberExpression filed, Number value) {
            return filed.loe(value);
        }
    },
    ;

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof NumberExpression expression) {
            return apply(expression, value.numberValue());
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(NumberExpression filed, Number value);

    public static NumberOperator from(String symbol) {
        for (NumberOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}