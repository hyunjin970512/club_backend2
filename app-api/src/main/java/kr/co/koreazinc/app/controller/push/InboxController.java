package kr.co.koreazinc.app.controller.push;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.push.InboxItemDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.push.InboxQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inbox")
public class InboxController {
	
	private final InboxQueryService inboxQueryService;
	private final CurrentUserService currentUserService;
	
	/**
	 * 알림 목록 조회
	 */
	@GetMapping
	public List<InboxItemDto> list(@RequestParam(defaultValue = "50") int size) {
		String empNo = currentUserService.empNoOrThrow();
		return inboxQueryService.list(empNo, size);
	}
	
	/**
	 * 읽음 처리
	 */
	@PostMapping("/{inboxId}/read")
	public void read(@PathVariable Long inboxId) {
		String empNo = currentUserService.empNoOrThrow();
		inboxQueryService.markRead(empNo, inboxId);
	}
	
	/**
	 * 읽지 않은 알림 개수
	*/
	@GetMapping("/unread-count")
	public Map<String, Long> unreadCount() {
		String empNo = currentUserService.empNoOrThrow();
		long cnt = inboxQueryService.countUnread(empNo);
		return Map.of("count", cnt);
	}
}
