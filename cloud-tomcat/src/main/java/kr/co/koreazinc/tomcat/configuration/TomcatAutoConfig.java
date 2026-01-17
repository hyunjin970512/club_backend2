package kr.co.koreazinc.tomcat.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.koreazinc.tomcat.embedded.CustomServerFactory;
import kr.co.koreazinc.tomcat.property.ApjProperty;
import lombok.RequiredArgsConstructor;

public class TomcatAutoConfig {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @RequiredArgsConstructor
    public static class TomcatServletConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

        private final ApjProperty properties;

        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new CustomServerFactory.Servlet();
        }

        @Override
        public void customize(final TomcatServletWebServerFactory factory) {
            // APJ Connector 추가
            if (properties.isEnabled()) factory.addAdditionalTomcatConnectors(properties.createConnector());
            // Tomcat8 이상 부터, parameter에 특수문자 전달 시 거부 됨, 전달받을 수 있도록 처리
            factory.addConnectorCustomizers(connector->connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    @RequiredArgsConstructor
    public static class TomcatReactiveConfiguration implements WebServerFactoryCustomizer<TomcatReactiveWebServerFactory> {

        private final ApjProperty properties;

        @Bean
        public TomcatReactiveWebServerFactory webServerFactory() {
            return new CustomServerFactory.Reactive();
        }

        @Override
        public void customize(final TomcatReactiveWebServerFactory factory) {
            // APJ Connector 추가
            if (properties.isEnabled()) factory.addAdditionalTomcatConnectors(properties.createConnector());
            // Tomcat8 이상 부터, parameter에 특수문자 전달 시 거부 됨, 전달받을 수 있도록 처리
            factory.addConnectorCustomizers(connector->connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}"));
        }
    }
}