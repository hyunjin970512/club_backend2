package kr.co.koreazinc.doc.v3.utils;

import java.net.URI;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import kr.co.koreazinc.doc.v3.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpecificationMapper {

    private static ObjectMapper objectMapper;

    private static synchronized ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            // module.addDeserializer(Type.class, new JsonDeserializer<Type>() {

            //     @Override
            //     public Type deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
            //         for(Type value : Type.values()) {
            //             if(p.getText().equalsIgnoreCase(value.toString())) {
            //                 return value;
            //             }
            //         }
            //         return null;
            //     }
            // });
            objectMapper.registerModule(module);
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static String writeValue(Object value) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            log.error("Error serializing value: {}", e.getMessage(), e);
        }
        return null;
    }

    public static OpenAPI parse(ClientHttpConnector connector, URI uri) {
        return WebClient.builder()
            .clientConnector(connector)
            .codecs(configurer->configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(getObjectMapper())))
            .build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(OpenAPI.class)
            .block();
    }
}