package kr.co.koreazinc.spring.http.enhancer;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.filter.AttributeContextFilter;
import kr.co.koreazinc.spring.property.PathProperty;
import kr.co.koreazinc.spring.utility.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpEnhancer {

    public static Servlet create(HttpServletRequest request, HttpServletResponse response) {
        return new Servlet(request, response);
    }

    public static Servlet create() {
        return new Servlet();
    }

    public static Servlet.Request create(HttpServletRequest request) {
        return new Servlet.Request(request);
    }

    public static Servlet.Response create(HttpServletResponse response) {
        return new Servlet.Response(response);
    }

    public static Reactive create(ServerWebExchange exchange) {
        return new Reactive(exchange);
    }

    public static Reactive.Request create(ServerHttpRequest request) {
        return new Reactive.Request(request);
    }

    public static Reactive.Response create(ServerHttpResponse response) {
        return new Reactive.Response(response);
    }

    // MARK: - Servlet
    public static class Servlet {

        private HttpServletRequest request;

        private HttpServletResponse response;

        public Servlet(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        public Servlet() throws IllegalStateException {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            this.request = attributes.getRequest();
            this.response = attributes.getResponse();
        }

        // MARK: - Servlet > Request
        public static class Request {

            private HttpServletRequest request;

            public Request(HttpServletRequest request) {
                this.request = request;
            }

            public URI getURI() {
                StringBuffer sb = request.getRequestURL();
                String queryString = request.getQueryString();
                if (StringUtils.hasText(queryString)) {
                    sb.append('?').append(queryString);
                }
                return URI.create(sb.toString());
            }

            public HttpHeaders getHeaders() {
                return Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(
                        Function.identity(),
                        h->Collections.list(request.getHeaders(h)),
                        (oldValue, newValue)->newValue,
                        HttpHeaders::new
                    ));
            }

            public Map<String, Object> getAttributes() {
                return Collections.list(request.getAttributeNames())
                    .stream()
                    .collect(Collectors.toMap(
                        Function.identity(),
                        request::getAttribute
                    ));
            }

            public <T> T getAttributeOrDefault(String name, T defaultValue) {
                return (T) this.getAttributes().getOrDefault(name, defaultValue);
            }

            public Optional<Cookie> getCookie(String cookieName) {
                List<Cookie> cookies = Arrays.asList(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]));
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        return Optional.of(cookie);
                    }
                }
                return Optional.empty();
            }
        }

        public Servlet.Request request() {
            return new Servlet.Request(this.request);
        }

        // MARK: - Servlet > Response
        public static class Response {

            private HttpServletResponse response;

            public Response(HttpServletResponse response) {
                this.response = response;
            }

            public boolean isCommitted() {
                return response.isCommitted();
            }

            public void redirect(URI location) throws IOException {
                redirect(location.toString());
            }

            public void redirect(String location) throws IOException {
                response.setStatus(HttpStatus.FOUND.value());
                response.setHeader(HttpHeaders.LOCATION, location);
            }

            public HttpStatusCode getStatusCode() {
                return HttpStatus.valueOf(response.getStatus());
            }

            public HttpStatus getStatus() {
                return HttpStatus.valueOf(response.getStatus());
            }

            public void setStatusCode(@NonNull HttpStatusCode status) {
                response.setStatus(status.value());
            }

            public void setStatus(@NonNull HttpStatus status) {
                response.setStatus(status.value());
            }

            public void addCookie(Cookie cookie) {
                response.addCookie(cookie);
            }

            public void delCookie(String cookieName) {
                this.addCookie(new Cookie(cookieName, null) {{
                    setMaxAge(0);
                }});
            }
        }

        public Servlet.Response response() {
            return new Servlet.Response(this.response);
        }

        public Optional<Cookie> getCookie(String cookieName) {
            return request().getCookie(cookieName);
        }

        public void addCookie(Cookie cookie) {
            response().addCookie(cookie);
        }

        public void delCookie(String cookieName) {
            response().delCookie(cookieName);
        }

        public void redirect(URI location) throws IOException {
            response().redirect(location);
        }

        public void redirect(String location) throws IOException {
            response().redirect(location);
        }

        public void forward(URI location) throws ServletException {
            forward(location.toString());
        }

        public void forward(String location) throws ServletException {
            if (!response().isCommitted()) {
                try {
                    request.getRequestDispatcher(location).forward(request, response);
                } catch (Exception exception) {
                    throw new ServletException(exception);
                }
            } else {
                log.trace("Cannot call forward() after the response has been committed");
            }
        }

        public void forward(Throwable exception) throws ServletException {
            if (request().getURI().getPath().equals(PathProperty.ERROR)) throw new ServletException(exception);
            request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request().getURI().getPath());
            request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exception);
            forward(PathProperty.ERROR);
        }

        public void forward(Throwable exception, HttpStatusCode status) throws ServletException {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status.value());
            forward(exception);
        }
    }

    // MARK: - Reactive
    public static class Reactive {

        private ServerWebExchange exchange;

        public Reactive(ServerWebExchange exchange) {
            this.exchange = exchange;
        }

        // MARK: - Reactive > Request
        public static class Request {

            private ServerHttpRequest request;

            public Request(ServerHttpRequest request) {
                this.request = request;
            }

            public URI getURI() {
                return request.getURI();
            }

            public HttpHeaders getHeaders() {
                return request.getHeaders();
            }

            public Map<String, Object> getAttributes() {
                return request.getAttributes();
            }

            public <T> T getAttributeOrDefault(String name, T defaultValue) {
                return (T) this.getAttributes().getOrDefault(name, defaultValue);
            }

            public Optional<HttpCookie> getCookie(String cookieName) {
                if (request.getCookies().containsKey(cookieName)) {
                    return request.getCookies().get(cookieName).stream().findAny();
                }
                return Optional.empty();
            }
        }

        public Reactive.Request request() {
            return new Reactive.Request(exchange.getRequest());
        }

        // MARK: - Reactive > Response
        public static class Response {

            private ServerHttpResponse response;

            public Response(ServerHttpResponse response) {
                this.response = response;
            }

            public boolean isCommitted() {
                return response.isCommitted();
            }

            public Mono<Void> redirect(URI location) {
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(location);
                return response.setComplete();
            }

            public Mono<Void> redirect(String location) {
                return redirect(URI.create(location));
            }

            public HttpStatusCode getStatusCode() {
                return response.getStatusCode();
            }

            public HttpStatus getStatus() {
                return HttpStatus.valueOf(response.getStatusCode().value());
            }

            public void setStatusCode(@NonNull HttpStatusCode status) {
                response.setStatusCode(status);
            }

            public void setStatus(@NonNull HttpStatus status) {
                response.setStatusCode(status);
            }

            public void addCookie(ResponseCookie cookie) {
                response.addCookie(cookie);
            }

            public void delCookie(String cookieName) {
                this.addCookie(ResponseCookie.from(cookieName).maxAge(0).build());
            }
        }

        public Reactive.Response response() {
            return new Reactive.Response(exchange.getResponse());
        }

        public Optional<HttpCookie> getCookie(String cookieName) {
            return request().getCookie(cookieName);
        }

        public HttpHeaders getHeaders() {
            return request().getHeaders();
        }

        public void addCookie(ResponseCookie cookie) {
            response().addCookie(cookie);
        }

        public void delCookie(String cookieName) {
            response().delCookie(cookieName);
        }

        public Mono<Void> redirect(URI location) {
            return response().redirect(location);
        }

        public Mono<Void> redirect(String location) {
            return response().redirect(location);
        }

        public Mono<Void> forward(URI location) {
            if (!response().isCommitted()) {
                return WebClient.builder()
                    .codecs(configurer -> {
                        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024); // 2MB
                        configurer.defaultCodecs().enableLoggingRequestDetails(true);
                    })
                    .build()
                    .method(exchange.getRequest().getMethod())
                    .uri(binder->binder
                        .scheme(StringUtils.hasText(PropertyUtils.getProperty("server.ssl.key-store")) ? "https" : "http")
                        .userInfo(request().getURI().getUserInfo())
                        .host("localhost")
                        .port(PropertyUtils.getProperty("server.port", "8080"))
                        .path(location.getPath())
                        .query(location.getQuery())
                        .fragment(location.getFragment())
                        .build()
                    )
                    .httpRequest(clientRequest->{
                        ServerHttpRequest serverRequest = exchange.getRequest();
                        // Header 복사
                        clientRequest.getHeaders().addAll(serverRequest.getHeaders());
                        // Attribute 복사
                        clientRequest.getHeaders().put(AttributeContextFilter.CONTEXT_KEY, List.of(exchange.getRequest().getId()));
                    })
                    // TODO: Body 복사
                    // .body(exchange.getRequest().getBody(), DataBuffer.class)
                    .exchangeToMono(clientResponse->{
                        ServerHttpResponse serverResponse = exchange.getResponse();
                        // Status Code 복사
                        serverResponse.setStatusCode(clientResponse.statusCode());
                        // Header 복사
                        // Content-Length 제거
                        HttpHeaders clientHeaders = clientResponse.headers().asHttpHeaders();
                        String transferEncoding = clientHeaders.getFirst(HttpHeaders.TRANSFER_ENCODING);
                        if (StringUtils.hasText(transferEncoding) && "chunked".equalsIgnoreCase(transferEncoding.trim())) {
                            clientHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                        }
                        // Transfer-Encoding 제거
                        if (!clientHeaders.containsKey(HttpHeaders.TRANSFER_ENCODING) && clientHeaders.containsKey(HttpHeaders.CONTENT_LENGTH)) {
                            serverResponse.getHeaders().remove(HttpHeaders.TRANSFER_ENCODING);
                        }
                        // CORS 정책 제거
                        for (String key : clientHeaders.keySet()) {
                            if (key.startsWith("Access-Control") || key.equals(HttpHeaders.VARY)) continue;
                            serverResponse.getHeaders().addAll(key, clientHeaders.get(key));
                        }
                        // Body 복사
                        return clientResponse.bodyToMono(byte[].class)
                            .flatMap(body->{
                                return serverResponse.writeWith(Mono.just(serverResponse.bufferFactory().wrap(body)));
                            });
                    })
                    .onErrorResume(throwable->{
                        log.warn("Forwarding error[{} -> {}]: {}", exchange.getRequest().getURI(), location, throwable.getMessage());
                        return Mono.error(throwable);
                    });
            } else {
                log.trace("Cannot call forward() after the response has been committed");
            }
            return exchange.getResponse().setComplete();
        }

        public Mono<Void> forward(String location) {
            return forward(URI.create(location));
        }

        public Mono<Void> forward(Throwable exception) {
            if (request().getURI().getPath().equals(PathProperty.ERROR)) return Mono.error(exception);
            exchange.getRequest().getAttributes().put(RequestDispatcher.ERROR_REQUEST_URI, request().getURI().getPath());
            exchange.getRequest().getAttributes().put(RequestDispatcher.ERROR_EXCEPTION, exception);
            return forward(PathProperty.ERROR);
        }

        public Mono<Void> forward(Throwable exception, HttpStatusCode status) {
            exchange.getRequest().getAttributes().put(RequestDispatcher.ERROR_STATUS_CODE, status.value());
            return forward(exception);
        }
    }
}