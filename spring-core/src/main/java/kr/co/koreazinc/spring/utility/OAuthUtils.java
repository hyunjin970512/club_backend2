package kr.co.koreazinc.spring.utility;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.reactive.function.client.WebClient;

import com.auth0.jwk.*;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.spring.exception.TokenIssuanceException;
import kr.co.koreazinc.spring.util.OAuth;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@UtilityClass
public class OAuthUtils {

    private static final ObjectMapper OM = new ObjectMapper();

    public String issuedToken(String tokenUrl, String clientId, String clientSecret, String scope) {
        try {
            String raw = WebClient.create()
                .post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)

                // ✅ 서버가 BasicAuth 요구할 수도 있음 (이거 하나로 판가름 난다)
                .headers(h -> h.setBasicAuth(clientId, clientSecret))

                // ✅ 그래도 폼에도 같이 넣어둠(서버 구현마다 다름)
                .body(fromFormData("grant_type", "client_credentials")
                    .with("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("scope", scope))

                .exchangeToMono(resp ->
                    resp.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            int code = resp.statusCode().value();
                            log.info("[OAUTH] tokenUrl={}", tokenUrl);
                            log.info("[OAUTH] status={}, body={}", code, body);

                            if (code >= 400) {
                                return Mono.error(new RuntimeException("token issuance failed: " + code + " / " + body));
                            }
                            return Mono.just(body);
                        })
                )
                .block();

            OAuth dto = OM.readValue(raw, OAuth.class);

            if (dto == null || dto.getAccessToken() == null || dto.getAccessToken().isBlank()) {
                throw new RuntimeException("access_token missing. raw=" + raw);
            }
            return dto.getAccessToken();

        } catch (Exception e) {
            log.error("OAuthUtils - issuedToken failed: {}", e.getMessage(), e);
            throw new TokenIssuanceException();
        }
    }


    public boolean validationToken(String oAuth, String discoveryUrl) {
        try {
            DecodedJWT jwt = JWT.decode(oAuth);
            JwkProvider provider = new UrlJwkProvider(new URL(discoveryUrl));
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
            return true;
        } catch (JWTDecodeException | MalformedURLException | JwkException | SignatureVerificationException e) {
            log.error("OAuthUtils - validationToken: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> parseToken(String oAuth, String discoveryUrl, String clientUrl) throws AuthenticationServiceException {
        try {
            DecodedJWT jwt = JWT.decode(oAuth);
            JwkProvider provider = new UrlJwkProvider(new URL(discoveryUrl));
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
            try {
                JWTVerifier verifier = JWT.require(algorithm).withAudience(clientUrl).build();
                Map<String, Object> claims = new HashMap<>(verifier.verify(oAuth).getClaims());
                return claims;
            } catch (TokenExpiredException | InvalidClaimException e) {
                log.error("OAuthUtils - parseToken: " + e.getMessage());
                throw new AuthenticationServiceException(e.getMessage());
            }
        } catch (JWTDecodeException | MalformedURLException | JwkException | SignatureVerificationException e) {
            log.error("OAuthUtils - parseToken: " + e.getMessage());
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
}