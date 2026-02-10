package kr.co.koreazinc.app.service.push;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.koreazinc.app.model.push.AppPushPayloadDto;
import kr.co.koreazinc.spring.model.PushInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.spring.utility.PushUtils;
import kr.co.koreazinc.temp.repository.push.PushSubscriptionAppRepository; // 니 repo 패키지에 맞춰
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppPushSender {

    private static final String PROJECT_ID = "groupportal-ba29a";

    private final PushSubscriptionAppRepository appRepo;
    private final OAuth2Property oauth2;

    public int sendToEmpNo(String empNo, AppPushPayloadDto tpl) {

        // active token들 조회
        var tokens = appRepo.findByEmpNoAndActiveYn(empNo, "Y");
        if (tokens == null || tokens.isEmpty()) return 0;

        // message = push
        OAuth2Property.Credential credential = oauth2.getCredential("message");

        int ok = 0;
        for (var t : tokens) {
            try {
                PushInfo info = PushInfo.builder()
                        .projectId(PROJECT_ID)
                        .token(t.getToken())
                        .title(tpl.getTitle())
                        .content(tpl.getContent())
                        .build();

                PushUtils.remoteSend(credential, info);
                ok++;
            } catch (Exception e) {
                log.error("[APP-PUSH] fail empNo={}, tokenId={}, err={}",
                        empNo, t.getId(), e.toString());
            }
        }
        return ok;
    }

    public int sendToEmpNos(List<String> empNos, AppPushPayloadDto tpl) {
        int sum = 0;
        for (String empNo : empNos) sum += sendToEmpNo(empNo, tpl);
        return sum;
    }
}
