package kr.co.koreazinc.data.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataType {

    public static final String TEXT = "text";

    public static final String INTEGER = "integer";

    public static final String SMALLINT = "smallint";

    public static final String TINYINT = "tinyint";

    public static final String BIGINT = "bigint";

    public static final String UUID = "uuid";

    public static final String JSONB = "jsonb";

    public static final String TIMESTAMP = "timestamp";

    public static final String BOOLEAN = "boolean";

    public static final String VARCHAR = "varchar";

    public static final String NVARCHAR = "nvarchar";

    public static final String NCHAR = "nchar";

    public static final String CHAR = "char";

    public static final String INT = "int";

    public static final String DATETIME = "datetime";

    public static final String SMALLDATETIME = "smalldatetime";

    public static final String DATE = "date";

    public static final String NUMERIC = "numeric";

    public static final String FLOAT = "float";

    public static final String DECIMAL = "decimal";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Array {

        public static final String TEXT = DataType.TEXT + "[]";

        public static final String UUID = DataType.UUID + "[]";

        public static final String JSONB = DataType.JSONB + "[]";
    }
}