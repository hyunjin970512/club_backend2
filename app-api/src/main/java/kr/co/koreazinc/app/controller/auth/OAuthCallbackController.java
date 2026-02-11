package kr.co.koreazinc.app.controller.auth;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import kr.co.koreazinc.app.service.auth.EmpUpsertService;
import kr.co.koreazinc.spring.security.model.ResponseToken;
import kr.co.koreazinc.spring.security.utility.AuthenticationTokenUtils;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.spring.utility.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthCallbackController {

  private final RestTemplate restTemplate;
  private final OAuth2Property oauth2;
  
  // SSO 로그인 후 DB 데이터 저장 및 업데이트
  private final EmpUpsertService empUpsertService;

  /**
   * ✅ 표준앱 방식 (쿠키명/만료는 AuthenticationTokenUtils 기준)
   * - Access  : ACCESS-TOKEN  (httpOnly=false)
   * - Refresh : REFRESH-TOKEN (httpOnly=true)
   */

  @GetMapping("/callback")
  public ResponseEntity<Void> callback(@RequestParam("code") String code, HttpServletRequest request) {

    log.info("[OAUTH] callback code={}", StringUtils.isNotBlank(code));

    OAuth2Property.Provider p = oauth2.getProvider(OAuth2Property.Provider.AUTH);

    // redirect_uri는 yml의 "{URL}/oauth/callback" 기반으로 request로 생성
    String redirectUri = oauth2.getClient().getRedirect().getLoginURL(request);

    // token/userinfo URL 만들기 (base-url + path)
    String tokenUrl = p.getBaseUrl() + p.getTokenUrl();
    String userinfoUrl = p.getBaseUrl() + p.getUserInfoUrl();

    // 1) code -> 회사 access_token
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("code", code);
    form.add("redirect_uri", redirectUri);
    form.add("client_id", oauth2.getClient().getId());
    // secret 필요한 SSO면 여기 추가
    // form.add("client_secret", oauth2.getClient().getSecret());

    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    @SuppressWarnings("unchecked")
    Map<String, Object> tokenRes =
        restTemplate.postForObject(tokenUrl, new HttpEntity<>(form, h), Map.class);

    String companyAccessToken = toNonNullString(tokenRes == null ? null : tokenRes.get("access_token"));
    if (companyAccessToken == null) {
      log.warn("[OAUTH] company access_token missing");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 2) 회사 access_token -> userinfo
    HttpHeaders uh = new HttpHeaders();
    uh.setBearerAuth(companyAccessToken);

    @SuppressWarnings("unchecked")
    Map<String, Object> me = restTemplate.exchange(
        userinfoUrl, HttpMethod.GET, new HttpEntity<>(uh), Map.class
    ).getBody();

    
    empUpsertService.upsertFromSsoMap(me);
    
    String userId = toNonNullString(me == null ? null : me.get("userId"));
    String empNo = extractEmpNoFromJob(me);

    log.info("[OAUTH] userId={}, empNo={}", userId, empNo);

    if (empNo == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 3) ✅ JWT 발급 (표준앱 핵심: empNo claim 포함)
    // 만료 정책은 AuthenticationTokenUtils의 expires 설정값 그대로 사용
    Map<String, Object> claims = Map.of(
        "empNo", empNo,
        "userId", userId == null ? "" : userId
    );

    String accessJwt = JwtUtils.createToken(
        claims,
        JwtUtils.getPrivateKey(),
        AuthenticationTokenUtils.ACCESS_TOKEN_EXPIRES_IN * 1000L
    );

    String refreshJwt = JwtUtils.createToken(
        claims,
        JwtUtils.getPrivateKey(),
        AuthenticationTokenUtils.REFRESH_TOKEN_EXPIRES_IN * 1000L
    );

    // 4) ✅ 쿠키 저장 (표준앱 규칙)
    boolean secure = isSecureRequest(request);

    ResponseCookie accessCookie = ResponseCookie.from(AuthenticationTokenUtils.ACCESS_TOKEN_COOKIE_NAME, accessJwt)
        .httpOnly(false)                 // ✅ access는 프론트가 읽을 수도 있는 정책
        .secure(secure)
        .sameSite("Lax")
        .path("/")
        .maxAge(AuthenticationTokenUtils.ACCESS_TOKEN_EXPIRES_IN)
        .build();

    ResponseCookie refreshCookie = ResponseCookie.from(AuthenticationTokenUtils.REFRESH_TOKEN_COOKIE_NAME, refreshJwt)
        .httpOnly(true)                  // ✅ refresh는 무조건 true
        .secure(secure)
        .sameSite("Lax")
        .path("/")
        .maxAge(AuthenticationTokenUtils.REFRESH_TOKEN_EXPIRES_IN)
        .build();

    // 5) ✅ 리다이렉트 (표준앱처럼: 세션 requestURI 우선)
    String redirectUrl = buildRedirectUrl(request);

    HttpHeaders out = new HttpHeaders();
    out.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
    out.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    out.add(HttpHeaders.LOCATION, redirectUrl);

    return new ResponseEntity<>(out, HttpStatus.FOUND);
  }

  /**
   * ✅ 표준앱식 refresh 엔드포인트
   * - refresh 쿠키 검증되면 access 재발급 + access 쿠키만 재세팅
   */
  @PostMapping("/refresh")
  public ResponseEntity<Void> refresh(HttpServletRequest request) {

    String refreshToken = AuthenticationTokenUtils.getRefreshToken(request);
    if (StringUtils.isBlank(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    if (!AuthenticationTokenUtils.validationRefreshToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // refresh 토큰의 claim 기반으로 access 재발급
    @SuppressWarnings("unchecked")
    Map<String, Object> claims = JwtUtils.parseToken(refreshToken, JwtUtils.getPublicKey());
    if (claims == null || StringUtils.isBlank(String.valueOf(claims.get("empNo")))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String newAccess = JwtUtils.createToken(
        claims,
        JwtUtils.getPrivateKey(),
        AuthenticationTokenUtils.ACCESS_TOKEN_EXPIRES_IN * 1000L
    );

    boolean secure = isSecureRequest(request);

    ResponseCookie accessCookie = ResponseCookie.from(AuthenticationTokenUtils.ACCESS_TOKEN_COOKIE_NAME, newAccess)
        .httpOnly(false)
        .secure(secure)
        .sameSite("Lax")
        .path("/")
        .maxAge(AuthenticationTokenUtils.ACCESS_TOKEN_EXPIRES_IN)
        .build();

    HttpHeaders out = new HttpHeaders();
    out.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
    return new ResponseEntity<>(out, HttpStatus.NO_CONTENT);
  }

  // -------------------------
  // helpers
  // -------------------------

  private boolean isSecureRequest(HttpServletRequest request) {
    // dev는 http일 수 있으니 강제 false로 박고 싶으면 여기서 고정해도 됨
    // return false;
    return request.isSecure();
  }

  private String buildRedirectUrl(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    String requestURI = null;
    if (session != null) {
      Object v = session.getAttribute("requestURI");
      if (v != null) requestURI = String.valueOf(v);
      session.removeAttribute("requestURI");
    }

    // 기본 이동 경로
    if (StringUtils.isBlank(requestURI)) requestURI = "/main";

    // 프론트가 리액트(3000)면 그대로 유지
    String host = request.getHeader("Host");
    if (StringUtils.isBlank(host)) host = request.getServerName();
    host = host.replaceAll(":\\d+$", "");

    // 운영/개발 분기 필요하면 여기서 조절
    // 개발: http://host:3000 + requestURI
    // 운영: https://host + requestURI
    return "http://" + host + ":3000" + requestURI;
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
    return toNonNullString(job0.get("empNo"));
  }

  private String toNonNullString(Object v) {
    if (v == null) return null;
    String s = String.valueOf(v).trim();
    if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
    return s;
  }
}
