package kr.co.koreazinc.data.types.operator;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateTimeExpression;

import kr.co.koreazinc.data.types.FilterOperator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@SuppressWarnings("unchecked")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DateTimeOperator implements FilterOperator {
    EQUALS("=") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.eq(value);
        }
    },
    NOT_EQUALS("<>") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.ne(value);
        }
    },
    GREATER(">") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.gt(value);
        }
    },
    GREATER_OR_EQUAL(">=") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.goe(value);
        }
    },
    LESS("<") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.lt(value);
        }
    },
    LESS_OR_EQUAL("<=") {

        @Override
        public BooleanExpression apply(DateTimeExpression filed, LocalDateTime value) {
            return filed.loe(value);
        }
    }
    ;

    private final String symbol;

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value) {
        if (filed instanceof DateTimeExpression expression) {
            return apply(expression, LocalDateTime.parse(value.asText()));
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + filed.getClass().getSimpleName());
    }

    public abstract BooleanExpression apply(DateTimeExpression filed, LocalDateTime value);

    public static DateTimeOperator from(String symbol) {
        for (DateTimeOperator operator : values()) {
            if (operator.symbol.equalsIgnoreCase(symbol)) {
                return operator;
            }
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported operator: " + symbol);
    }
}
