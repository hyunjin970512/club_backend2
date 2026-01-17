package kr.co.koreazinc.data.types.operator;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateExpression;

import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DateOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.ne(value);
        }
    },
    GREATER(">") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.gt(value);
        }
    },
    GREATER_OR_EQUAL(">=") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.goe(value);
        }
    },
    LESS("<") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.lt(value);
        }
    },
    LESS_OR_EQUAL("<=") {

        @Override
        public BooleanExpression apply(DateExpression filed, LocalDate value) {
            return filed.loe(value);
        }
    }
    ;

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof DateExpression expression) {
            return apply(expression, LocalDate.parse(value.asText()));
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(DateExpression filed, LocalDate value);

    public static DateOperator from(String symbol) {
        for (DateOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}