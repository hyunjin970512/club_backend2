package kr.co.koreazinc.spring.advice;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.http.utility.UriUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class GlobalExceptionAdvice {

    @ControllerAdvice
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @RequiredArgsConstructor
    public static class GlobalExceptionServletAdvice {

        @ExceptionHandler({kr.co.koreazinc.spring.web.servlet.exception.NotFoundException.class, org.springframework.web.servlet.resource.NoResourceFoundException.class})
        public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.web.servlet.resource.NoResourceFoundException exception) throws ServletException {
            HttpEnhancer.create(request, response).forward(exception, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws ServletException {
            HttpEnhancer.create(request, response).forward(exception, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ResponseStatusException.class)
        public void handle(HttpServletRequest request, HttpServletResponse response, ResponseStatusException exception) throws ServletException {
            HttpEnhancer.create(request, response).forward(exception, exception.getStatusCode());
        }

        @ExceptionHandler(Exception.class)
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception exception) throws ServletException {
            HttpEnhancer.create(request, response).forward(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ModelAttribute
        public void injector(HttpServletRequest request, HttpServletResponse response, Model model) {
            // Global Model Attribute
            model.addAttribute("baseURI", UriUtils.toBaseURI(HttpEnhancer.create(request).getURI()));
        }
    }

    @ControllerAdvice
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    @RequiredArgsConstructor
    public static class GlobalExceptionReactiveAdvice {

        @ExceptionHandler({kr.co.koreazinc.spring.web.reactive.exception.NotFoundException.class, org.springframework.web.reactive.resource.NoResourceFoundException.class})
        public Mono<Void> handle(ServerWebExchange exchange, org.springframework.web.reactive.resource.NoResourceFoundException exception) {
            return HttpEnhancer.create(exchange).forward(exception, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception) {
            return HttpEnhancer.create(exchange).forward(exception, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ResponseStatusException.class)
        public Mono<Void> handle(ServerWebExchange exchange, ResponseStatusException exception) {
            return HttpEnhancer.create(exchange).forward(exception, exception.getStatusCode());
        }

        @ExceptionHandler(Exception.class)
        public Mono<Void> handle(ServerWebExchange exchange, Exception exception) {
            return HttpEnhancer.create(exchange).forward(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ModelAttribute
        public void injector(ServerWebExchange exchange, Model model) {
            // Global Model Attribute
            model.addAttribute("baseURI", UriUtils.toBaseURI(exchange.getRequest().getURI()));
        }
    }
}