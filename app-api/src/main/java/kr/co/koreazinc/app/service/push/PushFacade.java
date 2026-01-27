package kr.co.koreazinc.app.service.push;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.push.PushPayloadDto;
import kr.co.koreazinc.app.model.push.PushType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PushFacade {

	private final PushTemplateService templateService;
	private final InboxNotifyService inboxNotifyService;
	private final WebPushSendService webPushSendService;
	private final InboxSseHub inboxSseHub;
	
	/**
	 * 공통 푸시 발송
	 */
	public void send(
						PushType type,
						List<String> targetEmpNos,
						Map<String, Object> data,
						String createdByEmpNo
					) {
	
		if (type == null || targetEmpNos == null || targetEmpNos.isEmpty()) {
			return;
		}
	
		// 1️ 대상 정리
		List<String> targets = targetEmpNos.stream()
											.filter(Objects::nonNull)
											.map(String::trim)
											.filter(s -> !s.isEmpty())
											.distinct()
											.toList();
			
		if (targets.isEmpty()) return;
			
		// 2️ 템플릿 생성
		PushPayloadDto payload = templateService.build(type, data);
		if (payload == null) return;
			
		// 3️ DB 저장(트랜잭션) + 커밋 후 훅 등록
		inboxNotifyService.notifyInboxAndAfterCommit(
														type.name(),
														targets,
														payload.getTitle(),
														payload.getBody(),
														payload.getLinkUrl(),
														payload.getPayloadJson(),
														createdByEmpNo,
														() -> {
															// 4 SSE
															inboxSseHub.publishToEmpNos(targets, payload);
															
															webPushSendService.sendToEmpNos(
																							targets,
																							payload.getTitle(),
																							payload.getBody(),
																							payload.getLinkUrl(),
																							payload.getPayloadJson()
																							);
														}
													);
		
	}
	
}
