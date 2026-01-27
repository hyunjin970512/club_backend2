package kr.co.koreazinc.app.service.push;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.app.model.push.PushPayloadDto;
import kr.co.koreazinc.app.model.push.PushType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushTemplateService {

	private final ObjectMapper om;
	
	public PushPayloadDto build(PushType type, Map<String, Object> data) {
		return switch (type) {
			case CLUB_JOIN_REQUEST -> payload(
				v(data,"clubNm") + " 가입 신청",
				v(data,"applicantNm") + "님이 가입 신청했습니다.",
				"/club/join/requests?clubId=" + v(data,"clubId"),
				map("type","CLUB_JOIN_REQUEST","clubId", v(data,"clubId"))
			);
			
			case CLUB_JOIN_APPROVED -> payload(
				v(data,"clubNm") + " 가입 승인",
				"가입이 승인되었습니다.",
				"/club?clubId=" + v(data,"clubId"),
				map("type","CLUB_JOIN_APPROVED","clubId", v(data,"clubId"))
			);
			
			case CLUB_JOIN_REJECTED -> payload(
				v(data,"clubNm") + " 가입 거절",
				"가입이 거절되었습니다.",
				"/club?clubId=" + v(data,"clubId"),
				map("type","CLUB_JOIN_REJECTED","clubId", v(data,"clubId"))
			);
			
			case POST_CREATED -> payload(
				"[" + v(data,"clubNm") + "] 새 게시글",
				v(data,"authorNm") + " : " + v(data,"postTitle"),
				"/club/board?clubId=" + v(data,"clubId") + "&postId=" + v(data,"postId"),
				map("type","POST_CREATED","clubId", v(data,"clubId"), "postId", v(data,"postId"))
			);
			
			case COMMENT_CREATED -> payload(
				"새 댓글",
				v(data,"commenterNm") + "님이 댓글을 남겼습니다.",
				"/club/board?clubId=" + v(data,"clubId") + "&postId=" + v(data,"postId"),
				map("type","COMMENT_CREATED","postId", v(data,"postId"))
			);
			
			case REPLY_CREATED -> payload(
				"새 대댓글",
				v(data,"replierNm") + "님이 대댓글을 남겼습니다.",
				"/club/board?clubId=" + v(data,"clubId") + "&postId=" + v(data,"postId"),
				map("type","REPLY_CREATED","postId", v(data,"postId"))
			);
		};
	}

	private PushPayloadDto payload(String title, String body, String linkUrl, Map<String, Object> payloadObj) {
		try {
			String payloadJson = om.writeValueAsString(payloadObj);
			return new PushPayloadDto(title, body, linkUrl, payloadJson);
		} catch (JsonProcessingException e) {
			return new PushPayloadDto("알림", "알림이 도착했습니다.", "/main", "{\"type\":\"DEFAULT\"}");
		}
	}
	
	private Map<String, Object> map(Object... kv) {
		Map<String, Object> m = new HashMap<>();
		for (int i = 0; i < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
		return m;
	}
	
	private String v(Map<String, Object> data, String key) {
		Object val = data.get(key);
		return val == null ? "" : String.valueOf(val);
	}
}
