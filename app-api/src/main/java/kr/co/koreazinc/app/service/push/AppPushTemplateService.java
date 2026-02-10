package kr.co.koreazinc.app.service.push;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.app.model.push.AppPushPayloadDto;
import kr.co.koreazinc.app.model.push.PushType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppPushTemplateService {

    private final ObjectMapper om;

    public AppPushPayloadDto build(PushType type, Map<String, Object> data) {
        return switch (type) {

            // ===== 동호회 가입 =====
            case CLUB_JOIN_REQUEST -> payload(
                    "[" + v(data, "clubNm") + "] 가입 신청",
                    v(data, "applicantNm") + "님이 가입 신청했습니다.",
                    base(type, "clubId", v(data, "clubId"))
            );

            case CLUB_JOIN_APPROVED -> payload(
                    "[" + v(data, "clubNm") + "] 가입 승인",
                    "가입이 승인되었습니다.",
                    base(type, "clubId", v(data, "clubId"))
            );

            case CLUB_JOIN_REJECTED -> payload(
                    "[" + v(data, "clubNm") + "] 가입 거절",
                    "가입이 거절되었습니다.",
                    base(type, "clubId", v(data, "clubId"))
            );

            // ===== 동호회 게시판 =====
            case POST_CREATED -> payload(
                    "[" + v(data, "clubNm") + "] 새 게시물",
                    v(data, "authorNm") + " : " + v(data, "postTitle"),
                    base(type, "clubId", v(data, "clubId"), "postId", v(data, "postId"))
            );

            case COMMENT_CREATED -> payload(
                    "새 댓글",
                    v(data, "commenterNm") + "님이 댓글을 남겼습니다.",
                    base(type, "clubId", v(data, "clubId"), "postId", v(data, "postId"), "commentId", v(data, "commentId"))
            );

            case REPLY_CREATED -> payload(
                    "새 대댓글",
                    v(data, "commenterNm") + "님이 대댓글을 남겼습니다.",
                    base(type, "clubId", v(data, "clubId"), "postId", v(data, "postId"), "commentId", v(data, "commentId"), "replyId", v(data, "replyId"))
            );

            // ===== 투게더 =====
            case POST_CREATED_TO -> payload(
                    "[" + v(data, "typeCd") + "투게더] 새 게시물",
                    v(data, "authorNm") + " : " + v(data, "postTitle"),
                    base(type, "boardId", v(data, "boardId"), "postId", v(data, "postId"))
            );

            case COMMENT_CREATED_TO -> payload(
                    "새 댓글",
                    v(data, "commenterNm") + "님이 댓글을 남겼습니다.",
                    base(type, "boardId", v(data, "boardId"), "postId", v(data, "postId"), "commentId", v(data, "commentId"))
            );

            case REPLY_CREATED_TO -> payload(
                    "새 대댓글",
                    v(data, "commenterNm") + "님이 대댓글을 남겼습니다.",
                    base(type, "boardId", v(data, "boardId"), "postId", v(data, "postId"), "commentId", v(data, "commentId"), "replyId", v(data, "replyId"))
            );
        };
    }

    private AppPushPayloadDto payload(String title, String content, Map<String, Object> payloadObj) {
        try {
            String payloadJson = om.writeValueAsString(payloadObj);
            return new AppPushPayloadDto(title, content, payloadJson);
        } catch (JsonProcessingException e) {
            return new AppPushPayloadDto("알림", "알림이 도착했습니다.", "{\"type\":\"DEFAULT\"}");
        }
    }

    private Map<String, Object> base(PushType type, Object... kv) {
        Map<String, Object> m = new HashMap<>();
        m.put("type", type.name());
        for (int i = 0; i < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    private String v(Map<String, Object> data, String key) {
        Object val = data.get(key);
        return val == null ? "" : String.valueOf(val);
    }
}
