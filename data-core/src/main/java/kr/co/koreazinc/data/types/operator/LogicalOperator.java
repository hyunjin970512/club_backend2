package kr.co.koreazinc.data.types.operator;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LogicalOperator {
    AND("and") {

        @Override
        public BooleanExpression apply(BooleanExpression left, BooleanExpression right) {
            return left.and(right);
        }
    },
    OR("or") {

        @Override
        public BooleanExpression apply(BooleanExpression left, BooleanExpression right) {
            return left.or(right);
        }
    },
    NOT_AND("not and") {

        @Override
        public BooleanExpression apply(BooleanExpression left, BooleanExpression right) {
            return left.and(right).not();
        }
    },
    NOT_OR("not or") {

        @Override
        public BooleanExpression apply(BooleanExpression left, BooleanExpression right) {
            return left.or(right).not();
        }
    },
    ;

    private final String symbol;

    public abstract BooleanExpression apply(BooleanExpression left, BooleanExpression right);

    public static LogicalOperator from(String symbol) {
        for (LogicalOperator op : values()) {
            if (op.symbol.equalsIgnoreCase(symbol)) {
                return op;
            }
        }
        return null;
    }
}