package kr.co.koreazinc.app.service.push;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionWeb;

@Component
@RequiredArgsConstructor
public class WebPushSender {

	private final PushService pushService;
	private final ObjectMapper objectMapper;
	
	/**
	 * 실제 WebPush 전송
	 */
	public void send(
						PushSubscriptionWeb sub,
						String title,
						String body,
						String linkUrl,
						String payloadJson
	) throws Exception {
	
		// 1️ payload 구성 (브라우저에서 쓰는 형식)
		Map<String, Object> payload = new HashMap<>();
		payload.put("title", title);
		payload.put("body", body);
		payload.put("linkUrl", linkUrl);
		
		if(payloadJson != null && !payloadJson.isBlank()) {
			payload.put(
					"payload",
					objectMapper.readValue(payloadJson, Map.class)
					);
		}
		
		String json = objectMapper.writeValueAsString(payload);

		// 2️ subscription 정보
		Notification notification = new Notification(
														sub.getEndpoint(),
														sub.getP256dh(),
														sub.getAuth(),
														json
													);
	
		// 3 전송
		pushService.send(notification);
	}
}
