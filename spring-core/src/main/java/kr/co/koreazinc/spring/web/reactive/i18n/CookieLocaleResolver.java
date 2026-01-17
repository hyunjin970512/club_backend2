package kr.co.koreazinc.spring.web.reactive.i18n;

import java.time.Duration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieLocaleResolver extends AbstractLocaleContextResolver {

    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";

    public static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".TIME_ZONE";

    public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";

    private ResponseCookie cookie;

    private boolean languageTagCompliant = true;

    private boolean rejectInvalidCookies = true;

    private Function<ServerWebExchange, Locale> defaultLocaleFunction = exchange -> {
        Locale defaultLocale = getDefaultLocale();
        return (defaultLocale != null ? defaultLocale : Locale.getDefault());
    };

    private Function<ServerWebExchange, TimeZone> defaultTimeZoneFunction = exchange -> getDefaultTimeZone();

    public CookieLocaleResolver(String cookieName) {
        Assert.notNull(cookieName, "'cookieName' must not be null");
        this.cookie = ResponseCookie.from(cookieName).path("/").sameSite("Lax").build();
    }

    public CookieLocaleResolver() {
        this(DEFAULT_COOKIE_NAME);
    }

    public void setCookieMaxAge(Duration cookieMaxAge) {
        Assert.notNull(cookieMaxAge, "'cookieMaxAge' must not be null");
        this.cookie = this.cookie.mutate().maxAge(cookieMaxAge).build();
    }

    public void setCookiePath(@Nullable String cookiePath) {
        this.cookie = this.cookie.mutate().path(cookiePath).build();
    }

    public void setCookieDomain(@Nullable String cookieDomain) {
        this.cookie = this.cookie.mutate().domain(cookieDomain).build();
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookie = this.cookie.mutate().secure(cookieSecure).build();
    }

    public void setCookieHttpOnly(boolean cookieHttpOnly) {
        this.cookie = this.cookie.mutate().httpOnly(cookieHttpOnly).build();
    }

    public void setCookieSameSite(String cookieSameSite) {
        Assert.notNull(cookieSameSite, "cookieSameSite must not be null");
        this.cookie = this.cookie.mutate().sameSite(cookieSameSite).build();
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    public boolean isLanguageTagCompliant() {
        return this.languageTagCompliant;
    }

    public void setRejectInvalidCookies(boolean rejectInvalidCookies) {
        this.rejectInvalidCookies = rejectInvalidCookies;
    }

    public boolean isRejectInvalidCookies() {
        return this.rejectInvalidCookies;
    }

    public void setDefaultLocaleFunction(Function<ServerWebExchange, Locale> defaultLocaleFunction) {
        Assert.notNull(defaultLocaleFunction, "defaultLocaleFunction must not be null");
        this.defaultLocaleFunction = defaultLocaleFunction;
    }

    public void setDefaultTimeZoneFunction(Function<ServerWebExchange, TimeZone> defaultTimeZoneFunction) {
        Assert.notNull(defaultTimeZoneFunction, "defaultTimeZoneFunction must not be null");
        this.defaultTimeZoneFunction = defaultTimeZoneFunction;
    }


    // @Override
    // public Locale resolveLocale(ServerWebExchange exchange) {
    //     parseLocaleCookieIfNecessary(exchange);
    //     return (Locale) exchange.getRequest().getAttributes().get(LOCALE_REQUEST_ATTRIBUTE_NAME);
    // }

    @Override
    public LocaleContext resolveLocaleContext(final ServerWebExchange exchange) {
        parseLocaleCookieIfNecessary(exchange);
        return new TimeZoneAwareLocaleContext() {
            @Override
            @Nullable
            public Locale getLocale() {
                return (Locale) exchange.getRequest().getAttributes().get(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }
            @Override
            @Nullable
            public TimeZone getTimeZone() {
                return (TimeZone) exchange.getRequest().getAttributes().get(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseLocaleCookieIfNecessary(ServerWebExchange exchange) {
        if (!exchange.getRequest().getAttributes().containsKey(LOCALE_REQUEST_ATTRIBUTE_NAME)) {
            Locale locale = null;
            TimeZone timeZone = null;

            // Retrieve and parse cookie value.
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst(this.cookie.getName());
            if (cookie != null) {
                String value = cookie.getValue();
                String localePart = value;
                String timeZonePart = null;
                int separatorIndex = localePart.indexOf('/');
                if (separatorIndex == -1) {
                    // Leniently accept older cookies separated by a space...
                    separatorIndex = localePart.indexOf(' ');
                }
                if (separatorIndex >= 0) {
                    localePart = value.substring(0, separatorIndex);
                    timeZonePart = value.substring(separatorIndex + 1);
                }
                try {
                    locale = (!"-".equals(localePart) ? parseLocaleValue(localePart) : null);
                    if (timeZonePart != null) {
                        timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                    }
                }
                catch (IllegalArgumentException ex) {
                    if (isRejectInvalidCookies() && !exchange.getRequest().getAttributes().containsKey(WebUtils.ERROR_EXCEPTION_ATTRIBUTE)) {
                        throw new IllegalStateException("Encountered invalid locale cookie '" +
                                this.cookie.getName() + "': [" + value + "] due to: " + ex.getMessage());
                    }
                    else {
                        // Lenient handling (for example, error dispatch): ignore locale/timezone parse exceptions
                        if (log.isDebugEnabled()) {
                            log.debug("Ignoring invalid locale cookie '" + this.cookie.getName() +
                                    "': [" + value + "] due to: " + ex.getMessage());
                        }
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale +
                            "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }

            exchange.getRequest().getAttributes().put(LOCALE_REQUEST_ATTRIBUTE_NAME,
                (locale != null ? locale : this.defaultLocaleFunction.apply(exchange)));
            exchange.getRequest().getAttributes().put(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
                (timeZone != null ? timeZone : this.defaultTimeZoneFunction.apply(exchange)));
        }
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext localeContext) {

        Assert.notNull(exchange.getResponse(), "HttpServletResponse is required for CookieLocaleResolver");

        Locale locale = null;
        TimeZone zone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext timeZoneAwareLocaleContext) {
                zone = timeZoneAwareLocaleContext.getTimeZone();
            }
            String value = (locale != null ? toLocaleValue(locale) : "-") + (zone != null ? '/' + zone.getID() : "");
            this.cookie = this.cookie.mutate().value(value).build();
        }

        exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, this.cookie.toString());
        exchange.getRequest().getAttributes().put(LOCALE_REQUEST_ATTRIBUTE_NAME,
            (locale != null ? locale : this.defaultLocaleFunction.apply(exchange)));
        exchange.getRequest().getAttributes().put(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
            (zone != null ? zone : this.defaultTimeZoneFunction.apply(exchange)));
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }

    protected String toLocaleValue(Locale locale) {
        return (isLanguageTagCompliant() ? locale.toLanguageTag() : locale.toString());
    }
}