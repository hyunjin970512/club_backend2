package kr.co.koreazinc.app.controller.push;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.push.InboxSseHub;

/**
 * 인앱 알림 SSE 스트림
 * - 브라우저 열려 있을 때 실시간 알림
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class InboxSseController {

	private final InboxSseHub hub;
	private final CurrentUserService currentUserService;
	
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream() {
		String empNo = currentUserService.empNoOrThrow();
		return hub.connect(empNo);
	}
}
