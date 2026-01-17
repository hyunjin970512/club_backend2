package kr.co.koreazinc.data.strategy.physical;

import java.util.Locale;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CamelCaseToLowerUnderscoresNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    protected Identifier getIdentifier(String name, final boolean quoted, final JdbcEnvironment jdbcEnvironment) {
        if (isCaseInsensitive(jdbcEnvironment)) {
            name = name.toLowerCase(Locale.ROOT);
        }
        // quoted = 예약어 여부
        return new Identifier(name, true);
    }
}