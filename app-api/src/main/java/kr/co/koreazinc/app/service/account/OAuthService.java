package kr.co.koreazinc.app.service.account;

import kr.co.koreazinc.app.model.security.JwtProvider;
import kr.co.koreazinc.temp.model.entity.account.Employee;
import kr.co.koreazinc.temp.repository.account.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuthService {

    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.token-url}")
    private String tokenUrl;
    
    private final JwtProvider jwtProvider;
    private final EmployeeRepository employeeRepository;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loginWithAuthorizationCode(String code) {

        /* =========================
         * 1. 인가코드 → 토큰 교환
         * ========================= */
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);

        ResponseEntity<String> res = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .toEntity(String.class);

        System.out.println("TOKEN STATUS = " + res.getStatusCode());
        System.out.println("TOKEN BODY = " + res.getBody());

        if (!res.getStatusCode().is2xxSuccessful()
                || res.getBody() == null
                || res.getBody().isBlank()) {
            throw new RuntimeException("OAuth token exchange failed");
        }

        /* =========================
         * 2. access_token 파싱
         * ========================= */
        String accessToken;
        try {
            JsonNode tokenJson = objectMapper.readTree(res.getBody());
            accessToken = tokenJson.path("access_token").asText(null);
        } catch (Exception e) {
            throw new RuntimeException("Token JSON parse failed", e);
        }

        if (accessToken == null) {
            throw new RuntimeException("access_token is null");
        }

        /* =========================
         * 3. 사용자 정보 조회
         * ========================= */
        System.out.println("CALL USERINFO");

        ResponseEntity<String> userRes;
        try {
            userRes = restClient.get()
                    .uri("https://auth-dev.koreazinc.co.kr/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class);
        } catch (Exception e) {
            System.out.println("USERINFO CALL FAILED: " + e.getClass().getName());
            e.printStackTrace();
            throw e;
        }

        System.out.println("USERINFO STATUS = " + userRes.getStatusCode());
        System.out.println("USERINFO BODY = " + userRes.getBody());

        JsonNode userInfo;
        try {
            userInfo = objectMapper.readTree(userRes.getBody());
            System.out.println("userInfo = " + userInfo);
        } catch (Exception e) {
            throw new RuntimeException("UserInfo JSON parse failed", e);
        }


        /* =========================
         * 4. 사용자 정보 파싱 (핵심 수정)
         * ========================= */

        // 1️⃣ empNo 추출
        String empNoTmp = null;
        for (JsonNode job : userInfo.path("job")) {
            if ("Y".equals(job.path("bassYn").asText())) {
                empNoTmp = job.path("empNo").asText(null);
                break;
            }
        }

        // 2️⃣ nameKo 추출 (임시 변수)
        String nameKoTmp = userInfo.path("userKoNm").asText(null);
        if (nameKoTmp == null || nameKoTmp.isBlank()) {
            nameKoTmp = userInfo.path("userNm").asText("UNKNOWN");
        }

        // 3️⃣ 검증
        if (empNoTmp == null || empNoTmp.isBlank()) {
            throw new RuntimeException("empNo not found in userInfo: " + userInfo);
        }

        // 4️⃣ 람다용 final 변수
        final String empNo = empNoTmp;
        final String nameKo = nameKoTmp;


        /* =========================
         * 5. DB 매핑 (사번 PK)
         * ========================= */
//        Employee employee = employeeRepository
//                .findByEmpNoAndUseAtAndDeleteAt(empNo, "Y", "N")
//                .orElseGet(() -> employeeRepository.save(
//                        Employee.builder()
//                                .empNo(empNo)
//                                .nameKo(nameKo)
//                                .pwd("OAUTH")
//                                .useAt("Y")
//                                .deleteAt("N")
//                                .createUser(empNo)
//                                .createDate(LocalDateTime.now())
//                                .build()
//                ));

        /* =========================
         * 6. 우리 서비스 JWT 발급
         * ========================= */
        return jwtProvider.createToken(empNoTmp, "ROLE_USER");
    }
}
