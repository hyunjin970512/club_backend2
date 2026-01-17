package kr.co.koreazinc.data.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;

import kr.co.koreazinc.data.types.operator.DateOperator;
import kr.co.koreazinc.data.types.operator.DateTimeOperator;
import kr.co.koreazinc.data.types.operator.EnumOperator;
import kr.co.koreazinc.data.types.operator.NumberOperator;
import kr.co.koreazinc.data.types.operator.ObjectOperator;
import kr.co.koreazinc.data.types.operator.StringOperator;

public interface FilterOperator {

    public static FilterOperator from(ComparableExpressionBase<?> field, String operator) {
        if (field instanceof NumberExpression) {
            return NumberOperator.from(operator);
        }
        if (field instanceof StringExpression) {
            return StringOperator.from(operator);
        }
        if (field instanceof DateTimeExpression) {
            return DateTimeOperator.from(operator);
        }
        if (field instanceof DateExpression) {
            return DateOperator.from(operator);
        }
        if (field instanceof EnumExpression) {
            return EnumOperator.from(operator);
        }
        // TODO: ADD support for other field types if needed
        // ComparableExpression는 공통된 상속 클래스이므로 제일 마지막에 처리 되어야 함.
        if (field instanceof ComparableExpression) {
            return ObjectOperator.from(operator);
        }
        // TODO: Error handling for unsupported field types
        throw new IllegalArgumentException("Unsupported field type: " + field.getClass().getSimpleName());
    };

    public BooleanExpression apply(ComparableExpressionBase<?> filed, JsonNode value);
}