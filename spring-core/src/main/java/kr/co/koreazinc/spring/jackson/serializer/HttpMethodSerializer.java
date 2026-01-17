package kr.co.koreazinc.spring.jackson.serializer;

import java.io.IOException;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class HttpMethodSerializer extends JsonSerializer<HttpMethod> {

    @Override
    public void serialize(HttpMethod value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.name());
        }
    }
}