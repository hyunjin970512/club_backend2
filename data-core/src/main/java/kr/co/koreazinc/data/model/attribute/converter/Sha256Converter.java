package kr.co.koreazinc.data.model.attribute.converter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Sha256Converter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (StringUtils.hasText(attribute)) {
            return digest(attribute);
        }
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String data) {
        return data;
    }

    public String digest(String msg) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            for (byte b: md.digest()) {
              builder.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return builder.toString();
    }
}