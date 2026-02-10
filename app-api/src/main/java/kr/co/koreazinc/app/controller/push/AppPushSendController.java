package kr.co.koreazinc.app.controller.push;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.co.koreazinc.app.model.push.AppPushSendRequest;
import kr.co.koreazinc.app.model.push.AppPushTokenUpsertRequest;
import kr.co.koreazinc.app.service.push.AppPushSendService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push/app")
public class AppPushSendController {

    private final AppPushSendService appPushSendService;

    /**
     * 앱 토큰 등록/갱신
     */
    @PostMapping("/token")
    public ResponseEntity<?> upsertToken(@RequestBody AppPushTokenUpsertRequest req) {
        Long id = appPushSendService.upsertToken(req);
        return ResponseEntity.ok(Map.of("id", id, "empNo", req.getEmpNo()));
    }

    /**
     * 사번으로 푸시 전송 (title/content만 바꿔서)
     */
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody AppPushSendRequest req) {
        int sent = appPushSendService.sendToEmpNo(req);
        return ResponseEntity.ok(Map.of("empNo", req.getEmpNo(), "sentCount", sent));
    }
}
