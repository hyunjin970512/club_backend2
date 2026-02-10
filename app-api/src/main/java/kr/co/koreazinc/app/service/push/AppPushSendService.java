package kr.co.koreazinc.app.service.push;

import kr.co.koreazinc.app.model.push.AppPushSendRequest;
import kr.co.koreazinc.app.model.push.AppPushTokenUpsertRequest;
import kr.co.koreazinc.spring.model.PushInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.spring.utility.PushUtils;
import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionApp;
import kr.co.koreazinc.temp.repository.push.PushSubscriptionAppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppPushSendService {

    private static final String PROJECT_ID = "groupportal-ba29a";

    private final PushSubscriptionAppRepository repo;
    private final OAuth2Property oauth2Property;

    /**
     * 토큰 등록/갱신 (token unique 기준 업서트)
     */
    public Long upsertToken(AppPushTokenUpsertRequest req) {
        LocalDateTime now = LocalDateTime.now();

        PushSubscriptionApp entity = repo.findByToken(req.getToken())
                .map(existing -> {
                    existing.setEmpNo(req.getEmpNo());
                    existing.setDeviceType(req.getDeviceType());
                    existing.setUserAgent(req.getUserAgent());
                    existing.setActiveYn("Y");
                    existing.setUpdatedAt(now);
                    return existing;
                })
                .orElseGet(() -> PushSubscriptionApp.builder()
                        .empNo(req.getEmpNo())
                        .token(req.getToken())
                        .deviceType(req.getDeviceType())
                        .userAgent(req.getUserAgent())
                        .activeYn("Y")
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        return repo.save(entity).getId();
    }

    /**
     * 사번 기준 활성 토큰들에 표준앱 푸시 전송
     */
    public int sendToEmpNo(AppPushSendRequest req) {
        List<PushSubscriptionApp> tokens = repo.findByEmpNoAndActiveYn(req.getEmpNo(), "Y");
        if (tokens.isEmpty()) {
            log.warn("[APP-PUSH] no active token. empNo={}", req.getEmpNo());
            return 0;
        }

        OAuth2Property.Credential credential = oauth2Property.getCredential("message");

        int success = 0;
        for (PushSubscriptionApp t : tokens) {
            try {
                PushInfo info = PushInfo.builder()
                        .projectId(PROJECT_ID)
                        .token(t.getToken())
                        .title(req.getTitle())
                        .content(req.getContent())
                        .build();

                PushUtils.remoteSend(credential, info);
                success++;
            } catch (Exception e) {
                log.error("[APP-PUSH] send fail. empNo={}, tokenId={}, err={}",
                        req.getEmpNo(), t.getId(), e.toString());
                // 원하면 여기서 active_yn='N' 처리도 가능
            }
        }
        return success;
    }
    
    public int sendToEmpNos(List<String> empNos, String title, String body) {
        if (empNos == null || empNos.isEmpty()) return 0;

        OAuth2Property.Credential credential = oauth2Property.getCredential("message");

        int success = 0;
        for (String empNo : empNos) {
            if (empNo == null || empNo.isBlank()) continue;

            List<PushSubscriptionApp> tokens = repo.findByEmpNoAndActiveYn(empNo.trim(), "Y");
            if (tokens == null || tokens.isEmpty()) continue;

            for (PushSubscriptionApp t : tokens) {
                try {
                    PushInfo info = PushInfo.builder()
                            .projectId(PROJECT_ID)
                            .token(t.getToken())
                            .title(title)
                            .content(body)
                            .build();

                    PushUtils.remoteSend(credential, info);
                    success++;
                } catch (Exception e) {
                    log.error("[APP-PUSH] send fail. empNo={}, tokenId={}, err={}",
                            empNo, t.getId(), e.toString());
                }
            }
        }
        return success;
    }

}
