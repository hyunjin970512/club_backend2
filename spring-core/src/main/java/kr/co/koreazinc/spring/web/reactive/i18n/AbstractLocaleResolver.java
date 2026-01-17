package kr.co.koreazinc.spring.web.reactive.i18n;

import java.util.Locale;

import org.springframework.lang.Nullable;

public abstract class AbstractLocaleResolver {

    @Nullable
    private Locale defaultLocale;

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }
}