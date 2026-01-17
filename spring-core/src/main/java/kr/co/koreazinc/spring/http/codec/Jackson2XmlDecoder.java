package kr.co.koreazinc.spring.http.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CharBufferDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

public class Jackson2XmlDecoder extends AbstractJackson2Decoder {

    private static final CharBufferDecoder CHAR_BUFFER_DECODER = CharBufferDecoder.textPlainOnly(Arrays.asList(",", "\n"), false);

    private static final ResolvableType CHAR_BUFFER_TYPE = ResolvableType.forClass(CharBuffer.class);

    public Jackson2XmlDecoder() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    public Jackson2XmlDecoder(ObjectMapper mapper) {
        this(mapper, MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
    }

    public Jackson2XmlDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
    }

    @Override
    public Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType,
            @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

        Flux<DataBuffer> flux = Flux.from(input);
        if (mimeType == null) {
            return flux;
        }

        // Jackson asynchronous parser only supports UTF-8
        Charset charset = mimeType.getCharset();
        if (charset == null || StandardCharsets.UTF_8.equals(charset) || StandardCharsets.US_ASCII.equals(charset)) {
            return flux;
        }

        // Re-encode as UTF-8.
        MimeType textMimeType = new MimeType(MimeTypeUtils.TEXT_PLAIN, charset);
        Flux<CharBuffer> decoded = CHAR_BUFFER_DECODER.decode(input, CHAR_BUFFER_TYPE, textMimeType, null);
        return decoded.map(charBuffer -> DefaultDataBufferFactory.sharedInstance.wrap(StandardCharsets.UTF_8.encode(charBuffer)));
    }
}