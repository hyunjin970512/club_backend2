package kr.co.koreazinc.app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
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
    
}
