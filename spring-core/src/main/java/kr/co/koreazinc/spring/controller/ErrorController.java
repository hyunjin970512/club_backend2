package kr.co.koreazinc.spring.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.http.RequestWrapper;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.http.functional.HttpFunction;
import kr.co.koreazinc.spring.http.util.ErrorResponse;
import kr.co.koreazinc.spring.property.PathProperty;
import kr.co.koreazinc.spring.utility.PropertyUtils;
import kr.co.koreazinc.spring.web.servlet.result.view.Modeling;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ErrorController {

    @Controller
    @RequestMapping("${server.error.path:/error}")
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class ErrorServletController extends AbstractErrorController {

        public ErrorServletController(ErrorAttributes errorAttributes) {
            super(errorAttributes, Collections.emptyList());
        }

        private HttpFunction.Servlet<MediaType> typeFunction = (request, response)->{
            return MediaType.TEXT_HTML;
        };

        @Autowired(required = false)
        private void setTypeFunction(HttpFunction.Servlet<MediaType> typeFunction) {
            this.typeFunction = typeFunction;
        }

        @RequestMapping
        public void error(HttpServletRequest request, HttpServletResponse response) throws ServletException {
            RequestWrapper wrapper = new RequestWrapper(request);
            wrapper.addHeader(HttpHeaders.ACCEPT, typeFunction.apply(request, response).toString());
            HttpEnhancer.create(wrapper, response).forward(PathProperty.ERROR);
        }

        @RequestMapping(produces = { MediaType.TEXT_HTML_VALUE })
        public Modeling front(HttpServletRequest request, HttpServletResponse response) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpEnhancer.create(request));
            return Modeling.view(PropertyUtils.getProperty("server.error.page.path", "error"))
                .status(errorResponse.getStatus())
                .modelAttribute("error", errorResponse)
                .build();
        }

        @ResponseBody
        @RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
        public ResponseEntity<ErrorResponse> back(HttpServletRequest request, HttpServletResponse response) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpEnhancer.create(request));
            return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
        }
    }

    @Controller
    @RequestMapping("${server.error.path:/error}")
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class ErrorReactiveController /* implements ErrorWebExceptionHandler */ {

        private HttpFunction.Reactive<MediaType> typeFunction = (exchange)->{
            return Mono.just(MediaType.TEXT_HTML);
        };

        @Autowired(required = false)
        private void setTypeFunction(HttpFunction.Reactive<MediaType> typeFunction) {
            this.typeFunction = typeFunction;
        }

        @RequestMapping
        public Mono<Void> error(ServerWebExchange exchange) {
            return typeFunction.apply(exchange).flatMap(type->{
                exchange.getRequest().getHeaders().set(HttpHeaders.ACCEPT, type.toString());
                return HttpEnhancer.create(exchange).forward(PathProperty.ERROR);
            });
        }

        @RequestMapping(produces = { MediaType.TEXT_HTML_VALUE })
        public Mono<Rendering> front(ServerWebExchange exchange) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpEnhancer.create(exchange.getRequest()));
            return Mono.just(
                Rendering.view(PropertyUtils.getProperty("server.error.page.path", "error"))
                    .status(errorResponse.getStatus())
                    .modelAttribute("error", errorResponse)
                    .build()
            );
        }

        @ResponseBody
        @RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
        public Mono<ResponseEntity<ErrorResponse>> back(ServerWebExchange exchange) {
            ErrorResponse errorResponse = ErrorResponse.of(HttpEnhancer.create(exchange.getRequest()));
            return Mono.just(new ResponseEntity<>(errorResponse, errorResponse.getStatus()));
        }
    }
}