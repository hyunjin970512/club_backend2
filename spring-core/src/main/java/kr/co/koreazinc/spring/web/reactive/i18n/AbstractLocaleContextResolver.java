package kr.co.koreazinc.spring.web.reactive.i18n;

import java.util.TimeZone;

import org.springframework.lang.Nullable;
import org.springframework.web.server.i18n.LocaleContextResolver;

public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {

    @Nullable
    private TimeZone defaultTimeZone = TimeZone.getDefault();

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }
}