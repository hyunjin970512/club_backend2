package kr.co.koreazinc.spring.web.reactive.i18n;

import java.util.Locale;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import kr.co.koreazinc.spring.web.reactive.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocaleChangeInterceptor implements HandlerInterceptor {

    public static final String DEFAULT_PARAM_NAME = "locale";

    private String paramName = DEFAULT_PARAM_NAME;

    @Nullable
    private HttpMethod[] httpMethods;

    private boolean ignoreInvalidLocale = false;

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(@Nullable HttpMethod... httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Nullable
    public HttpMethod[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    @Override
    public boolean preHandle(ServerWebExchange exchange) {
        if (exchange.getRequest().getQueryParams().containsKey(getParamName())) {
            String newLocale = exchange.getRequest().getQueryParams().getFirst(getParamName());
            if (checkHttpMethod(exchange.getRequest().getMethod())) {
                Locale locale = parseLocaleValue(newLocale);
                // TODO Cookie에 저장(?)
            }
        }
        return true;
    }

    private boolean checkHttpMethod(HttpMethod currentMethod) {
        HttpMethod[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        }
        for (HttpMethod configuredMethod : configuredMethods) {
            if (configuredMethod.equals(currentMethod)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}