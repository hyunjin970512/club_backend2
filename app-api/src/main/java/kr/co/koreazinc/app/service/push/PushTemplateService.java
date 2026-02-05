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
					"[" + v(data,"clubNm") + "] 가입 신청",
				v(data,"applicantNm") + "님이 가입 신청했습니다.",
				"/detail/" + v(data,"clubId"),
				map("type","CLUB_JOIN_REQUEST","clubId", v(data,"clubId"))
			);
			
			case CLUB_JOIN_APPROVED -> payload(
				"[" + v(data,"clubNm") + "] 가입 승인",
				"가입이 승인되었습니다.",
				"/detail/" + v(data,"clubId"),
				map("type","CLUB_JOIN_APPROVED","clubId", v(data,"clubId"))
			);
			
			case CLUB_JOIN_REJECTED -> payload(
				"[" + v(data,"clubNm") + "] 가입 거절",
				"가입이 거절되었습니다.",
				"/detail/" + v(data,"clubId"),
				map("type","CLUB_JOIN_REJECTED","clubId", v(data,"clubId"))
			);
			
			//동호회
			case POST_CREATED -> payload(
				"[" + v(data,"clubNm") + "] 새로운 게시물이 등록되었습니다.",
				v(data,"authorNm") + " : " + v(data,"postTitle"),
				"/detail/" + v(data,"clubId") + "/post/" + v(data,"postId"),
				map("type","POST_CREATED","clubId", v(data,"clubId"), "postId", v(data,"postId"))
			);
			
			case COMMENT_CREATED -> payload(
				"새 댓글",
				v(data,"commenterNm") + "님이 댓글을 남겼습니다.",
				"/detail/" + v(data,"clubId") + "/post/" + v(data,"postId"),
				map("type","COMMENT_CREATED","postId", v(data,"postId"))
			);
			
			case REPLY_CREATED -> payload(
				"새 대댓글",
				v(data,"commenterNm") + "님이 대댓글을 남겼습니다.",
				"/detail/" + v(data,"clubId") + "/post/" + v(data,"postId"),
				map("type","REPLY_CREATED","postId", v(data,"postId"))
			);
			
			//투게더	http://localhost:3000/together/9/post
			case POST_CREATED_TO -> payload(
				"[" + v(data,"typeCd") + "투게더] 새로운 게시물이 등록되었습니다.",
				v(data,"authorNm") + " : " + v(data,"postTitle"),
				"/together/" + v(data,"boardId") + "/post",
				map("type","POST_CREATED_TO","boardId", v(data,"boardId"))
			);
			
			case COMMENT_CREATED_TO -> payload(
				"새 댓글",
				v(data,"commenterNm") + "님이 댓글을 남겼습니다.",
				"/together/" + v(data,"boardId") + "/post",
				map("type","COMMENT_CREATED_TO","boardId", v(data,"boardId"))
			);
			
			case REPLY_CREATED_TO -> payload(
				"새 대댓글",
				v(data,"commenterNm") + "님이 대댓글을 남겼습니다.",
				"/together/" + v(data,"boardId") + "/post",
				map("type","REPLY_CREATED_TO","boardId", v(data,"boardId"))
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
