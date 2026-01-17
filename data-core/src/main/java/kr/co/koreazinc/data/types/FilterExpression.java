package kr.co.koreazinc.data.types;

import java.util.Stack;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import kr.co.koreazinc.data.types.operator.LogicalOperator;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FilterExpression {

    private Stack<LogicalOperator> operators = new Stack<>();
    private Stack<BooleanExpression> operands = new Stack<>();

    public FilterExpression push(String operator) {
        this.operators.push(LogicalOperator.from(operator));
        return this;
    }

    public FilterExpression push(BooleanExpression operand) {
        this.operands.push(operand);
        return this;
    }

    public BooleanExpression calculate() {
        // TODO: Error handling for empty expressions
        if (operands.isEmpty()) {
            return FilterExpression.of(false);
        }
        while (operators.size() > 0) {
            LogicalOperator operator = operators.pop();
            BooleanExpression right = operands.pop();
            BooleanExpression left = operands.pop();
            operands.push(operator.apply(left, right));
        }
        return operands.pop();
    }

    public static BooleanExpression of(boolean value) {
        return Expressions.asBoolean(value).isTrue();
    }
}