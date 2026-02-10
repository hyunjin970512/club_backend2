package kr.co.koreazinc.app.service.push;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.co.koreazinc.temp.model.entity.push.PushInbox;

import kr.co.koreazinc.app.model.push.PushPayloadDto;
import kr.co.koreazinc.temp.model.entity.push.PushMessage;

@Component
public class InboxSseHub {

  // empNo -> 여러 탭/여러 기기 동시 연결 지원
	private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
	
	// 1시간
	private static final long TIMEOUT_MS = 60 * 60 * 1000L;
	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	public SseEmitter connect(String empNo) {
		SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
			
		emitters.computeIfAbsent(empNo, k -> new CopyOnWriteArrayList<>()).add(emitter);
			
		// 연결 종료/오류 시 정리
		Runnable cleanup = () -> removeEmitter(empNo, emitter);
		emitter.onCompletion(cleanup);
		emitter.onTimeout(cleanup);
		emitter.onError(e -> cleanup.run());
			
		// 첫 연결 확인용 이벤트
		try {
			emitter.send(SseEmitter.event().name("connected").data("ok"));
		} catch (Exception e) {
			cleanup.run();
		}
			
		return emitter;
	}

	public void publishToEmpNos(List<String> empNos, PushPayloadDto payload) {
		if (empNos == null || empNos.isEmpty() || payload == null) return;
			
		Map<String, Object> data = new HashMap<>();
		data.put("title", payload.getTitle());
		data.put("body", payload.getBody());
		data.put("linkUrl", payload.getLinkUrl());
		data.put("payloadJson", payload.getPayloadJson());
		//data.put("createdAt", payload.getCreatedAt());
			
		send(empNos, data);
	}
	
	public void broadcast(List<String> empNos, PushMessage msg) {
		if (empNos == null || empNos.isEmpty() || msg == null) return;
		
		Map<String, Object> payload = new HashMap<>();
		payload.put("messageId", msg.getId());
		payload.put("eventType", msg.getEventType());
		payload.put("title", msg.getTitle());
		payload.put("body", msg.getBody());
		payload.put("linkUrl", msg.getLinkUrl());
		payload.put("payloadJson", msg.getPayloadJson());
		payload.put(
					"createdAt",
					msg.getCreatedAt() != null ? ISO.format(msg.getCreatedAt()) : null
		);
		send(empNos, payload);
	}
	
	private void send(List<String> empNos, Map<String, Object> payload) {
		for (String empNo : empNos) {
			if (empNo == null || empNo.isBlank()) continue;
			
			List<SseEmitter> list = emitters.get(empNo);
			if (list == null || list.isEmpty()) continue;
			
			List<SseEmitter> snapshot = new ArrayList<>(list);
			
			for (SseEmitter emitter : snapshot) {
				try {
				    emitter.send(SseEmitter.event().name("push").data(payload));
				} catch (Exception e) {
				    removeEmitter(empNo, emitter);
				}
			}
		}
	}

	private void removeEmitter(String empNo, SseEmitter emitter) {
		CopyOnWriteArrayList<SseEmitter> list = emitters.get(empNo);
		if (list == null) return;
			
		list.remove(emitter);
			
		if (list.isEmpty()) {
			emitters.remove(empNo);
		}
	}
	
	// 해당 empNo가 SSE 연결중인지
	public boolean isOnline(String empNo) {
	    if (empNo == null || empNo.isBlank()) return false;
	    List<SseEmitter> list = emitters.get(empNo);
	    return list != null && !list.isEmpty();
	}

	// 여러 명 중 온라인인 사람 제외하고(=오프라인만) 반환
	public List<String> offlineOnly(List<String> empNos) {
	    if (empNos == null || empNos.isEmpty()) return List.of();
	    return empNos.stream()
	            .filter(e -> e != null && !e.isBlank())
	            .map(String::trim)
	            .filter(e -> !e.isEmpty())
	            .filter(e -> !isOnline(e))
	            .distinct()
	            .toList();
	}
	
	public void broadcastInbox(List<PushInbox> inboxRows, PushMessage msg) {
	    if (inboxRows == null || inboxRows.isEmpty() || msg == null) return;

	    for (PushInbox inbox : inboxRows) {
	        String empNo = inbox.getEmpNo();
	        if (empNo == null || empNo.isBlank()) continue;

	        List<SseEmitter> list = emitters.get(empNo);
	        if (list == null || list.isEmpty()) continue;

	        Map<String, Object> payload = new HashMap<>();
	        payload.put("inboxId", inbox.getId());          // ✅ 핵심
	        payload.put("messageId", msg.getId());
	        payload.put("eventType", msg.getEventType());
	        payload.put("title", msg.getTitle());
	        payload.put("body", msg.getBody());
	        payload.put("linkUrl", msg.getLinkUrl());
	        payload.put("payloadJson", msg.getPayloadJson());
	        payload.put("createdAt",
	            msg.getCreatedAt() != null ? ISO.format(msg.getCreatedAt()) : null
	        );

	        List<SseEmitter> snapshot = new ArrayList<>(list);
	        for (SseEmitter emitter : snapshot) {
	            try {
	                emitter.send(SseEmitter.event().name("push").data(payload));
	            } catch (Exception e) {
	                removeEmitter(empNo, emitter);
	            }
	        }
	    }
	}


}
