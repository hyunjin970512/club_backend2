package kr.co.koreazinc.app.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    private static final String ACCESS_TOKEN_COOKIE_LEGACY = "ACCESS-TOKEN";

    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.login-url}")
    private String loginUrl;

    @GetMapping("/login")
    public void ssoLogin(HttpServletResponse response) throws IOException {
        String authUrl = UriComponentsBuilder
                .fromHttpUrl(loginUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("prompt", "login")
                .build()
                .toUriString();

        response.sendRedirect(authUrl);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie c1 = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .path("/").maxAge(0)
            .httpOnly(true).secure(false).sameSite("Lax")
            .build();

        ResponseCookie c2 = ResponseCookie.from(ACCESS_TOKEN_COOKIE_LEGACY, "")
            .path("/").maxAge(0)
            .httpOnly(true).secure(false).sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, c1.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, c2.toString());
        return ResponseEntity.ok().build();
    }
}
