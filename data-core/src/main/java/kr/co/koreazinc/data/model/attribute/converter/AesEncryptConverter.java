package kr.co.koreazinc.data.model.attribute.converter;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EncryptConverter;

@Converter
public class AesEncryptConverter extends EncryptConverter {

    public AesEncryptConverter() {
        super(new SecretKeySpec(Optional.ofNullable(System.getProperty("jpa.converter.encrypt.key")).orElse("aes256secretkey0").getBytes(StandardCharsets.UTF_8), "AES"));
    }
}