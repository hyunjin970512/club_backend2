package kr.co.koreazinc.data.support.attribute;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EncryptConverter implements AttributeConverter<String, String> {

    private final SecretKeySpec secretKey;

    protected EncryptConverter(SecretKeySpec secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String data) {
        return decrypt(data);
    }

    private String encrypt(String plainText) {
        if (StringUtils.hasText(plainText)) {
            try {
                try {
                    Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(secretKey.getEncoded()));
                    return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
                } catch (InvalidAlgorithmParameterException e) {
                    Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
                }
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                log.error("EncryptConverter - encrypt: " + e.getMessage());
            } catch (Exception e) {
                log.error("EncryptConverter - decrypt: " + e.getMessage());
            }
        }
        return plainText;
    }

    private String decrypt(String encryptedIvText) {
        if (StringUtils.hasText(encryptedIvText)) {
            try {
                try {
                    Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(secretKey.getEncoded()));
                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedIvText)), StandardCharsets.UTF_8);
                } catch (InvalidAlgorithmParameterException e) {
                    Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedIvText)), StandardCharsets.UTF_8);
                }
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                log.error("EncryptConverter - decrypt: " + e.getMessage());
            } catch (Exception e) {
                log.error("EncryptConverter - decrypt: " + e.getMessage());
            }
        }
        return encryptedIvText;
    }
}