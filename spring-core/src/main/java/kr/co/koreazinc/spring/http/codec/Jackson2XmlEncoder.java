package kr.co.koreazinc.spring.http.codec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Jackson2XmlEncoder extends AbstractJackson2Encoder {

    private static final List<MimeType> problemDetailMimeTypes = Collections.singletonList(MediaType.APPLICATION_PROBLEM_XML);

    @Nullable
    private final PrettyPrinter ssePrettyPrinter;

    public Jackson2XmlEncoder() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    public Jackson2XmlEncoder(ObjectMapper mapper) {
        this(mapper, MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
    }

    public Jackson2XmlEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        setStreamingMediaTypes(Arrays.asList(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml")));
        this.ssePrettyPrinter = initSsePrettyPrinter();
    }

    private static PrettyPrinter initSsePrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        return printer;
    }

    @Override
    protected List<MimeType> getMediaTypesForProblemDetail() {
        return problemDetailMimeTypes;
    }

    @Override
    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType,
            ResolvableType elementType, @Nullable Map<String, Object> hints) {

        return (this.ssePrettyPrinter != null &&
                MediaType.TEXT_EVENT_STREAM.isCompatibleWith(mimeType) &&
                writer.getConfig().isEnabled(SerializationFeature.INDENT_OUTPUT) ?
                writer.with(this.ssePrettyPrinter) : writer);
    }
}