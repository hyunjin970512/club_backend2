package kr.co.koreazinc.spring.utility;

import java.io.IOException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.core.io.ClassPathResource;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import kr.co.koreazinc.spring.exception.JwtIssuanceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {

    private static final Map<String, BiFunction<PublicKey, PrivateKey, Algorithm>> ALGORITHM_MAP = Map.of(
        "RS256", (publicKey, privateKey) -> Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey),
        "RS384", (publicKey, privateKey) -> Algorithm.RSA384((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey),
        "RS512", (publicKey, privateKey) -> Algorithm.RSA512((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey),
        "ES256", (publicKey, privateKey) -> Algorithm.ECDSA256((ECPublicKey) publicKey, (ECPrivateKey) privateKey),
        "ES384", (publicKey, privateKey) -> Algorithm.ECDSA384((ECPublicKey) publicKey, (ECPrivateKey) privateKey),
        "ES512", (publicKey, privateKey) -> Algorithm.ECDSA512((ECPublicKey) publicKey, (ECPrivateKey) privateKey)
    );

    public static PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource("security/private.der");
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(resource.getContentAsByteArray()));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException  e) {
            log.error("JwtUtils - getPrivateKey: " + e.getMessage(), e);
        }
        return null;
    }

    public static PublicKey getPublicKey() {
        try {
            ClassPathResource resource = new ClassPathResource("security/public.der");
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(resource.getContentAsByteArray()));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException  e) {
            log.error("JwtUtils - getPublicKey: " + e.getMessage(), e);
        }
        return null;
    }

    public static String createToken(Map<String, Object> claims, PrivateKey key, Long tokenValidMillisecond) throws JwtIssuanceException {
        return JWT.create()
            // .withIssuer("your-issuer")     // iss (발행자) 클레임
            // .withSubject(userId)           // sub (주제)   클레임
            // .withAudience("your-audience") // aud (대상자) 클레임
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + tokenValidMillisecond))
            .withPayload(claims)
            .sign(Algorithm.RSA256(null, (RSAPrivateKey) key));
    }

    public static boolean validationToken(String token, PublicKey key) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            JWTVerifier verifier = JWT.require(ALGORITHM_MAP.get(jwt.getAlgorithm()).apply(key, null)).build();
            verifier.verify(token);
            return true;
        } catch (IllegalArgumentException | JWTVerificationException exception) {
            log.error("JwtUtils - validationToken: " + exception.getMessage());
        } catch (Exception e) {
            log.error("JwtUtils - validationToken: " + e.getMessage(), e);
        }
        return false;
    }

    public static boolean validationToken(String token, URL url) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(url);
            PublicKey key = provider.get(jwt.getKeyId()).getPublicKey();
            return validationToken(token, key);
        } catch (JWTDecodeException | JwkException | SignatureVerificationException e) {
            log.error("JwtUtils - validationToken: " + e.getMessage());
        } catch (Exception e) {
            log.error("JwtUtils - validationToken: " + e.getMessage(), e);
        }
        return false;
    }

    public static Map<String, Object> parseToken(String token, PublicKey key) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            JWTVerifier verifier = JWT.require(ALGORITHM_MAP.get(jwt.getAlgorithm()).apply(key, null)).build();
            return verifier.verify(token).getClaims().entrySet().stream()
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().as(Object.class)), HashMap::putAll);
        } catch (IllegalArgumentException | JWTVerificationException e) {
            log.error("JwtUtils - parseToken: " + e.getMessage());
        } catch (Exception e) {
            log.error("JwtUtils - parseToken: " + e.getMessage(), e);
        }
        return new HashMap<>();
    }

    public static Map<String, Object> parseToken(String token, URL url) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(url);
            PublicKey key = provider.get(jwt.getKeyId()).getPublicKey();
            return parseToken(token, key);
        } catch (JWTDecodeException | JwkException | SignatureVerificationException e) {
            log.error("JwtUtils - parseToken: " + e.getMessage());
        } catch (Exception e) {
            log.error("JwtUtils - parseToken: " + e.getMessage(), e);
        }
        return new HashMap<>();
    }
}