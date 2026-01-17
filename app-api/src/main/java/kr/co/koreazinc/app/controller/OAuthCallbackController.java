package kr.co.koreazinc.app.controller;

import kr.co.koreazinc.app.service.account.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthCallbackController {

    private final OAuthService oauthService;

    // 로그인 후 메인으로 이동
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam("code") String code, HttpServletRequest request) throws Exception {
    	String host = request.getServerName();
        String jwt = oauthService.loginWithAuthorizationCode(code);

        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", jwt)
                .httpOnly(true)
                .secure(false)        // ✅ 로컬 http면 false (운영 https면 true)
                .path("/")
                .maxAge(60 * 60 * 2)
                .sameSite("Lax")      // ✅ localhost:3000 <-> localhost:8081 은 Lax로 충분
                .build();

        String redirectUrl = "http://" + host + ":3000/main";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.LOCATION, redirectUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
