package kr.co.koreazinc.spring.convert.converter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;

import kr.co.koreazinc.spring.convert.AbstractConverter;

public class UUIDConverter extends AbstractConverter<UUID> {

    public UUIDConverter() {
        super(UUID.randomUUID());
    }

    @Override
    public UUID process(@NonNull String source) {
        return Optional.of(source).map(UUID::fromString).orElse(null);
    }
}