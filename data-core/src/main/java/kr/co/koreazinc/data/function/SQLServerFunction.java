package kr.co.koreazinc.data.function;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;

public class SQLServerFunction {

    public static StringTemplate ltrim(StringPath expression) {
        return Expressions.stringTemplate("ltrim({0})", expression);
    }

    public static StringTemplate ltrim(StringPath expression, StringPath character) {
        return Expressions.stringTemplate("ltrim({0}, {1})", expression, character);
    }

    public static StringTemplate rtrim(StringPath expression) {
        return Expressions.stringTemplate("rtrim({0})", expression);
    }

    public static StringTemplate rtrim(StringPath expression, StringPath character) {
        return Expressions.stringTemplate("rtrim({0}, {1})", expression, character);
    }
}
