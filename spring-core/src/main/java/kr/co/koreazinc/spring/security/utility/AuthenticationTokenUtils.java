package kr.co.koreazinc.spring.security.utility;

import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.spring.security.model.ResponseToken;
import kr.co.koreazinc.spring.utility.JwtUtils;
import kr.co.koreazinc.spring.utility.PropertyUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthenticationTokenUtils {

    public final String ACCESS_TOKEN_COOKIE_NAME =
            PropertyUtils.getProperty("auth.cookie.access-token-name", "ACCESS-TOKEN");

    public final String REFRESH_TOKEN_COOKIE_NAME =
            PropertyUtils.getProperty("auth.cookie.refresh-token-name", "REFRESH-TOKEN");

    public final String LOGIN_HINT_COOKIE_NAME = "LOGIN-HINT";

    public final Integer ACCESS_TOKEN_EXPIRES_IN =
            NumberUtils.toInt(PropertyUtils.getProperty("auth.jwt.access-token-expires-in"), 12 * 60 * 60);

    public final Integer REFRESH_TOKEN_EXPIRES_IN =
            NumberUtils.toInt(PropertyUtils.getProperty("auth.jwt.refresh-token-expires-in"), 12 * 60 * 60);

    public final Integer LOGIN_HINT_EXPIRES_IN = 12 * 60 * 60;

    private ObjectMapper objectMapper = new ObjectMapper();

    // =========================
    // 토큰 생성/갱신
    // =========================
    public <T extends UserDetails> ResponseToken create(T userDetails) {
        String accessToken = JwtUtils.createToken(
                objectMapper.convertValue(userDetails, new TypeReference<Map<String, Object>>() {}),
                JwtUtils.getPrivateKey(),
                ACCESS_TOKEN_EXPIRES_IN * 1000L
        );
        String refreshToken = JwtUtils.createToken(
                objectMapper.convertValue(userDetails, new TypeReference<Map<String, Object>>() {}),
                JwtUtils.getPrivateKey(),
                REFRESH_TOKEN_EXPIRES_IN * 1000L
        );
        return new ResponseToken("Bearer", ACCESS_TOKEN_EXPIRES_IN, accessToken, REFRESH_TOKEN_EXPIRES_IN, refreshToken);
    }

    public <T extends UserDetails> ResponseToken refresh(String refreshToken, T userDetails) {
        String accessToken = JwtUtils.createToken(
                objectMapper.convertValue(userDetails, new TypeReference<Map<String, Object>>() {}),
                JwtUtils.getPrivateKey(),
                ACCESS_TOKEN_EXPIRES_IN * 1000L
        );
        return new ResponseToken("Bearer", ACCESS_TOKEN_EXPIRES_IN, accessToken, REFRESH_TOKEN_EXPIRES_IN, refreshToken);
    }

    // =========================
    // 토큰 검증/파싱
    // =========================
    public boolean validationAccessToken(String accessToken) {
        return JwtUtils.validationToken(accessToken, JwtUtils.getPublicKey());
    }

    public boolean validationRefreshToken(String refreshToken) {
        return JwtUtils.validationToken(refreshToken, JwtUtils.getPublicKey());
    }

    public <T extends UserDetails> T parseAccessToken(Class<T> type, String accessToken) {
        return objectMapper.convertValue(JwtUtils.parseToken(accessToken, JwtUtils.getPublicKey()), type);
    }

    public <T extends UserDetails> T parseRefreshToken(Class<T> type, String refreshToken) {
        return objectMapper.convertValue(JwtUtils.parseToken(refreshToken, JwtUtils.getPublicKey()), type);
    }

    // =========================
    // ✅ 요청에서 토큰 꺼내기 (추가한 핵심)
    // - 1) Authorization 헤더 (Bearer xxx 또는 xxx)
    // - 2) Cookie (ACCESS_TOKEN_COOKIE_NAME)
    // =========================
    public String getAccessToken(HttpServletRequest request) {
        // 1) Authorization header 우선
        String auth = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(auth)) {
            return auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        }

        // 2) Cookie
        return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName()) && StringUtils.isNotBlank(c.getValue())) {
                return c.getValue();
            }
        }
        return null;
    }
}
