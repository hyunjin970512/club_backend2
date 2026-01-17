package kr.co.koreazinc.temp.configuration;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;

public class CustomDialect extends SQLServerDialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        functionContributions.getFunctionRegistry().register("getCodeName",
                new StandardSQLFunction("[dbo].[getCodeName]", StandardBasicTypes.STRING));
    }

    public static StringTemplate getCodeName(StringPath mainCode, StringPath subCode) {
        return Expressions.stringTemplate("getCodeName({0}, {1})", mainCode, subCode);
    }
}
