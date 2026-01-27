package kr.co.koreazinc.app.service.push;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionWeb;
import kr.co.koreazinc.temp.repository.push.PushSubscriptionWebRepository;

/**
 * WebPush 실제 발송 담당
 *
 * 역할:
 * - empNo 기준으로 활성 구독 조회
 * - endpoint 별 WebPush 전송
 * - 실패(410 Gone 등) 시 구독 비활성 처리
 *
 * ❗ 외부(Service/Controller)에서 직접 호출 금지
 * ❗ 반드시 PushFacade를 통해서만 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushSendService {
	
	private final PushSubscriptionWebRepository subscriptionRepo;
	private final WebPushSubscriptionService subscriptionService;
	private final WebPushSender webPushSender;
	
	/**
	 * 여러 사번 WebPush 발송 (Facade 전용)
	 */
	public void sendToEmpNos(
							      List<String> empNos,
							      String title,
							      String body,
							      String linkUrl,
							      String payloadJson
							  ) {
		if (empNos == null || empNos.isEmpty()) return;
			
		for (String empNo : empNos) {
			try {
				sendToEmpNo(empNo, title, body, linkUrl, payloadJson);
			} catch (Exception e) {
				// TODO: handle exception
				log.warn("[WebPush] send failed empNo={}", empNo, e);
			}
		}
	}
	
	/**
	 * 사번 기준 WebPush 발송
	 */
	@Transactional
	public void sendToEmpNo(
								String empNo,
								String title,
								String body,
								String linkUrl,
								String payloadJson
							) {
	
		if (empNo == null || empNo.isBlank()) return;
			
		List<PushSubscriptionWeb> subs = subscriptionRepo.findActiveByEmpNo(empNo);
			
		if (subs == null || subs.isEmpty()) return;
			
		// 2️ endpoint 별 발송
		for (PushSubscriptionWeb sub : subs) {
			sendToSubscription(sub, title, body, linkUrl, payloadJson);
		}
	}
  
	/**
	 * endpoint 단위 발송
	 */
	private void sendToSubscription(
										PushSubscriptionWeb sub,
										String title,
										String body,
										String linkUrl,
										String payloadJson
									) {
		try {
		    webPushSender.send(sub, title, body, linkUrl, payloadJson);
		} catch (Exception e) {
		    // ❗ 대부분 여기서 410 Gone / 404
		    log.info("[WebPush] deactivate endpoint={}", sub.getEndpoint());
		    subscriptionService.deactivateByEndpoint(sub.getEndpoint());
		}
	}
			
}
