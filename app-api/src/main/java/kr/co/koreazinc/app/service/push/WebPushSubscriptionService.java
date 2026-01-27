package kr.co.koreazinc.app.service.push;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.push.WebPushSubscribeRequest;
import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionWeb;
import kr.co.koreazinc.temp.repository.push.PushSubscriptionWebRepository;

@Service
@RequiredArgsConstructor
public class WebPushSubscriptionService {

	private final PushSubscriptionWebRepository repo;
	
	/**
	 * ✅ endpoint 기준 구독 등록/갱신
	 * - 같은 브라우저(endpoint)로 재로그인/재구독 시
	 *   최신 empNo / key / userAgent 로 덮어쓴다
	 */
	@Transactional
	public void subscribe(String empNo, WebPushSubscribeRequest req) {
		if (empNo == null || empNo.isBlank()) return;
		if (req == null || req.getEndpoint() == null || req.getKeys() == null) return;
			
		String endpoint = trim(req.getEndpoint());
		if (endpoint.isEmpty()) return;
			
		String p256dh = trim(req.getKeys().getP256dh());
		String auth   = trim(req.getKeys().getAuth());
		if (p256dh.isEmpty() || auth.isEmpty()) return;
			
		String userAgent = trim(req.getUserAgent());
			
		// endpoint 기준 조회 (없으면 신규 생성)
		PushSubscriptionWeb sub =
				repo.findOneByEndpoint(endpoint)
					.orElseGet(() ->
						PushSubscriptionWeb.of(empNo, endpoint, p256dh, auth, userAgent)
					);
			
		// 기존 endpoint면 최신 정보로 갱신
		sub.activate(empNo, p256dh, auth, userAgent);
			
		// 영속/비영속 모두 커버 (명시적 save 유지)
		repo.save(sub);
	}
	
	/**
	 * ✅ 로그아웃/사용자 요청 기반 비활성화
	 * - 해당 사용자 + endpoint 조합만 끈다
	 */
	@Transactional
	public void deactivate(String empNo, String endpoint) {
		if (empNo == null || empNo.isBlank()) return;
			
		String ep = trim(endpoint);
		if (ep.isEmpty()) return;
			
		repo.findOneByEmpNoAndEndpoint(empNo, ep)
			.ifPresent(PushSubscriptionWeb::deactivate);
	}
	
	/**
	 * ✅ 발송 실패(410 Gone 등) 기반 비활성화
	 * - endpoint 하나를 통째로 끈다 (empNo 몰라도 됨)
	 */
	@Transactional
	public void deactivateByEndpoint(String endpoint) {
		String ep = trim(endpoint);
		if (ep.isEmpty()) return;
			
		repo.findOneByEndpoint(ep)
			.ifPresent(PushSubscriptionWeb::deactivate);
	}
	
	private String trim(String s) {
		return s == null ? "" : s.trim();
	}
}
