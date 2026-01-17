package kr.co.koreazinc.spring.security.utility;

import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;

import com.google.common.net.InternetDomainName;

import jakarta.servlet.http.Cookie;
import kr.co.koreazinc.spring.property.SpringProperty;
import kr.co.koreazinc.spring.utility.JwtUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationCookieUtils {

    // Domain
    // Path
    // Expires(MaxAge)
    // HttpOnly             네트워크 통신에서만 사용 여부
    // Secure               HTTPS에서만 사용 여부
    // SameSite
    //   - 'Strict'         발급한 도메인으로만 쿠키 전송 가능
    //   - 'Lax'            발급한 도메인, 안전한 요청에는 쿠키 전송 가능
    //   - 'None'           다른 도메인으로도 쿠키 전송 가능

    public static class SameSite {

        public static final String Strict = "Strict";

        public static final String Lax = "Lax";

        public static final String None = "None";
    }

    public static class Servlet {

        public static Cookie accessToken(String token, int expiry) {
            Cookie cookie = new Cookie(AuthenticationTokenUtils.ACCESS_TOKEN_COOKIE_NAME, token);
            cookie.setMaxAge(expiry);
            cookie.setHttpOnly(false);
            cookie.setSecure(!SpringProperty.IS_LOCAL);
            cookie.setAttribute("SameSite", SameSite.Lax);
            return cookie;
        }

        public static Cookie refreshToken(String token, int expiry) {
            Cookie cookie = new Cookie(AuthenticationTokenUtils.REFRESH_TOKEN_COOKIE_NAME, token);
            cookie.setMaxAge(expiry);
            cookie.setHttpOnly(true);
            cookie.setSecure(!SpringProperty.IS_LOCAL);
            cookie.setAttribute("SameSite", SameSite.Lax);
            return cookie;
        }

        public static Cookie loginHint(String domain, String userId) {
            String loginHint = JwtUtils.createToken(Map.of("userId", userId), JwtUtils.getPrivateKey(), AuthenticationTokenUtils.LOGIN_HINT_EXPIRES_IN * 1000L);
            Cookie cookie = new Cookie(AuthenticationTokenUtils.LOGIN_HINT_COOKIE_NAME, loginHint);
            cookie.setDomain(AuthenticationCookieUtils.topPrivateDomain(domain));
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(!SpringProperty.IS_LOCAL);
            cookie.setAttribute("SameSite", SameSite.Strict);
            return cookie;
        }

        public static String loginHint(Cookie cookie) {
            return String.valueOf(JwtUtils.parseToken(cookie.getValue(), JwtUtils.getPublicKey()).get("userId"));
        }
    }

    public static class Reactive {

        public static ResponseCookie accessToken(String token, int expiry) {
            return ResponseCookie.from(AuthenticationTokenUtils.ACCESS_TOKEN_COOKIE_NAME, token)
                .maxAge(expiry)
                .httpOnly(false)
                .secure(!SpringProperty.IS_LOCAL)
                .sameSite(SameSite.Strict)
                .build();
        }

        public static ResponseCookie refreshToken(String token, int expiry) {
            return ResponseCookie.from(AuthenticationTokenUtils.REFRESH_TOKEN_COOKIE_NAME, token)
                .maxAge(expiry)
                .httpOnly(true)
                .secure(!SpringProperty.IS_LOCAL)
                .sameSite(SameSite.Strict)
                .build();
        }

        public static ResponseCookie loginHint(String domain, String userId) {
            String loginHint = JwtUtils.createToken(Map.of("userId", userId), JwtUtils.getPrivateKey(), AuthenticationTokenUtils.LOGIN_HINT_EXPIRES_IN * 1000L);
            return ResponseCookie.from(AuthenticationTokenUtils.LOGIN_HINT_COOKIE_NAME, loginHint)
                .domain(AuthenticationCookieUtils.topPrivateDomain(domain))
                .path("/")
                .httpOnly(true)
                .secure(!SpringProperty.IS_LOCAL)
                .sameSite(SameSite.Strict)
                .build();
        }

        public static String loginHint(HttpCookie cookie) {
            return String.valueOf(JwtUtils.parseToken(cookie.getValue(), JwtUtils.getPublicKey()).get("userId"));
        }
    }

    private static String topPrivateDomain(String domain) {
        try {
            return InternetDomainName.from(domain).topPrivateDomain().toString();
        } catch(IllegalStateException e) {

        }
        return domain;
    }
}