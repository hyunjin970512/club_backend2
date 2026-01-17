package kr.co.koreazinc.spring.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headers = new HashMap<>();

    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = headers.get(name);
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Vector<String> headers = new Vector<>();
        String headerValue = getHeader(name);
        if (StringUtils.hasText(headerValue)) {
            headers.add(headerValue);
        }
        Collections.list(super.getHeaders(name)).forEach(headers::add);
        return headers.elements();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Vector<String> headerNames = new Vector<>(headers.keySet());
        Collections.list(super.getHeaderNames()).forEach(headerNames::add);
        return headerNames.elements();
    }
}