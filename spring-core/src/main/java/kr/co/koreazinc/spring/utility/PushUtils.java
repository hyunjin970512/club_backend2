package kr.co.koreazinc.spring.utility;

import java.util.HashMap;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import kr.co.koreazinc.spring.model.PushInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property.Credential;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PushUtils {

    public static void remoteSend(Credential credential, PushInfo info) {
        WebClient.builder()
                .baseUrl(credential.getBaseUrl())
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/push")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(
                        "Authorization",
                        "Bearer "
                                + OAuthUtils.issuedToken(
                                        credential.getTokenUrl(),
                                        credential.getClientId(),
                                        credential.getClientSecret(),
                                        credential.getScope()))
                .bodyValue(new HashMap<String, Object>() {
                    {
                        this.put("projectId", info.getProjectId());
                        this.put("token", info.getToken());
                        this.put("title", info.getTitle());
                        this.put("content", info.getContent());
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
