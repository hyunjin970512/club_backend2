package kr.co.koreazinc.app.controller.push;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.push.WebPushSubscribeRequest;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.push.WebPushSubscriptionService;

@RestController
@RequestMapping("/api/push/web")
@RequiredArgsConstructor
public class WebPushSubscriptionController {

	private final WebPushSubscriptionService subscriptionService;
	private final CurrentUserService currentUserService;
	
	@PostMapping("/subscribe")
	public void subscribe(@RequestBody WebPushSubscribeRequest req) {
		String empNo = currentUserService.empNoOrThrow();
		subscriptionService.subscribe(empNo, req);
	}
}
  