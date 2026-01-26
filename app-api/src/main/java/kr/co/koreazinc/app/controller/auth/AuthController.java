package kr.co.koreazinc.app.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

import kr.co.koreazinc.spring.security.property.OAuth2Property;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    private static final String ACCESS_TOKEN_COOKIE_LEGACY = "ACCESS-TOKEN";

    private final OAuth2Property oauth2;

    @GetMapping("/login")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // provider: auth
        OAuth2Property.Provider p = oauth2.getProvider(OAuth2Property.Provider.AUTH);

        // ✅ redirect_uri 는 {URL} 기반으로 자동 생성
        String redirectUri = oauth2.getClient().getRedirect().getLoginURL(request);

        // ✅ authorization endpoint (baseUrl + authorizationUrl)
        String authorizeUrl = p.getBaseUrl() + p.getAuthorizationUrl();

        String authUrl = UriComponentsBuilder
                .fromHttpUrl(authorizeUrl)
                .queryParam("response_type", "code")
                .queryParam("client_id", oauth2.getClient().getId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("prompt", "login")
                .build()
                .toUriString();

        
        log.info("authorizeUrl={}", authorizeUrl);
        log.info("redirectUri={}", redirectUri);
        log.info("authUrl={}", authUrl);
        
        
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
