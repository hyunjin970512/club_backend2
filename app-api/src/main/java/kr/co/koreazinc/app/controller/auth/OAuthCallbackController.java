package kr.co.koreazinc.app.controller.auth;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import kr.co.koreazinc.spring.utility.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthCallbackController {

  private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
  private static final String ACCESS_TOKEN_COOKIE_LEGACY = "ACCESS-TOKEN";

  @Value("${oauth.client-id}")
  private String clientId;

  @Value("${oauth.redirect-uri}")
  private String redirectUri;

  @Value("${oauth.token-url}")
  private String tokenUrl;

  @Value("${oauth.userinfo-url}")
  private String userinfoUrl;

  private final RestTemplate restTemplate;

  @GetMapping("/callback")
  public ResponseEntity<Void> callback(@RequestParam("code") String code, HttpServletRequest request) {

    log.info("code={}", code);

    // 1) code -> 회사 access_token
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("code", code);
    form.add("redirect_uri", redirectUri);
    form.add("client_id", clientId);

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    @SuppressWarnings("unchecked")
    Map<String, Object> tokenRes =
        restTemplate.postForObject(tokenUrl, new HttpEntity<>(form, h), Map.class);

    String companyAccessToken = toNonNullString(tokenRes == null ? null : tokenRes.get("access_token"));
    log.info("company token received={}", companyAccessToken != null);

    if (companyAccessToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 2) 회사 access_token -> userinfo
    HttpHeaders uh = new HttpHeaders();
    uh.setBearerAuth(companyAccessToken);

    @SuppressWarnings("unchecked")
    Map<String, Object> me = restTemplate.exchange(
        userinfoUrl, HttpMethod.GET, new HttpEntity<>(uh), Map.class
    ).getBody();

    log.info("userinfo keys={}", me == null ? null : me.keySet());

    // ✅ 여기서 empNo는 job[0].empNo 에서 뽑는다
    String userId = toNonNullString(me == null ? null : me.get("userId"));
    String empNo = extractEmpNoFromJob(me);

    log.info("userId={}, empNo={}", userId, empNo);

    if (empNo == null) {
      // empNo 없으면 우리 서비스는 인증 불가
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 3) 우리 JWT 발급 (empNo + userId 같이 넣어두면 나중에 유용)
    String myJwt = JwtUtils.createToken(
        Map.of("empNo", empNo, "userId", userId == null ? "" : userId),
        JwtUtils.getPrivateKey(),
        2 * 60 * 60 * 1000L
    );

    // 4) 쿠키 저장: ACCESS_TOKEN으로 통일
    ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, myJwt)
        .httpOnly(true)
        .secure(false)
        .sameSite("Lax")
        .path("/")
        .maxAge(60 * 60 * 2)
        .build();

    // 레거시 쿠키 청소
    ResponseCookie legacyCleanup = ResponseCookie.from(ACCESS_TOKEN_COOKIE_LEGACY, "")
        .httpOnly(true)
        .secure(false)
        .sameSite("Lax")
        .path("/")
        .maxAge(0)
        .build();

    //redirectUrl
    String reqHost = request.getHeader("Host");
    if (reqHost == null || reqHost.isBlank()) reqHost = request.getServerName();

    reqHost = reqHost.replaceAll(":\\d+$", ""); // 포트 제거

    String redirectUrl = "http://" + reqHost + ":3000/main";


    HttpHeaders out = new HttpHeaders();
    out.add(HttpHeaders.SET_COOKIE, cookie.toString());
    out.add(HttpHeaders.SET_COOKIE, legacyCleanup.toString());
    out.add(HttpHeaders.LOCATION, redirectUrl);
    return new ResponseEntity<>(out, HttpStatus.FOUND);
  }

  @SuppressWarnings("unchecked")
  private String extractEmpNoFromJob(Map<String, Object> me) {
    if (me == null) return null;

    Object jobObj = me.get("job");
    if (!(jobObj instanceof List)) return null;

    List<Object> jobList = (List<Object>) jobObj;
    if (jobList.isEmpty()) return null;

    Object first = jobList.get(0);
    if (!(first instanceof Map)) return null;

    Map<String, Object> job0 = (Map<String, Object>) first;
    // ✅ JSON 기준 키가 "empNo"
    return toNonNullString(job0.get("empNo"));
  }

  private String toNonNullString(Object v) {
    if (v == null) return null;
    String s = String.valueOf(v).trim();
    if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
    return s;
  }
}
