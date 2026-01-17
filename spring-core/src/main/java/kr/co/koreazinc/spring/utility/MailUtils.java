package kr.co.koreazinc.spring.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.koreazinc.spring.model.MailInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property.Credential;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailUtils {

    public static void remoteSend(Credential credential, MailInfo sendMail) {
        WebClient.builder()
            .baseUrl(credential.getBaseUrl())
            .build()
            .post()
            .uri(uriBuilder->uriBuilder.path(String.format("/v1/mail/%s", sendMail.getSender())).build())
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + OAuthUtils.issuedToken(credential.getTokenUrl(), credential.getClientId(), credential.getClientSecret(), credential.getScope()))
            .bodyValue(new HashMap<String, Object>()
                {{
                    this.put("from", sendMail.getFrom());
                    this.put("to", String.join(";", sendMail.getTo()));
                    this.put("cc", String.join(";", sendMail.getCc()));
                    this.put("bcc", String.join(";", sendMail.getBcc()));
                    this.put("title", sendMail.getSubject());
                    this.put("content", sendMail.getContent());
                    if (sendMail.hasAttachment()) {
                        List<Map<String, String>> attachments = new ArrayList<>();
                        for (File file : sendMail.getAttachments()) {
                            attachments.add(new HashMap<String, String>()
                                {{
                                    this.put("name", file.getName());
                                    try {
                                        this.put("contentBytes", FileUtils.encodeToBase64(file));
                                    } catch (IOException e) {
                                        log.error(e.getMessage());
                                    }
                                }}
                            );
                        }
                        this.put("attachments", attachments);
                    }
                }}
            )
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}