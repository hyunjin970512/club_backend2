package kr.co.koreazinc.app.service.push;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.temp.model.entity.push.PushInbox;
import kr.co.koreazinc.temp.model.entity.push.PushMessage;
import kr.co.koreazinc.temp.repository.push.PushInboxRepository;
import kr.co.koreazinc.temp.repository.push.PushMessageRepository;

@Service
@RequiredArgsConstructor
public class InboxNotifyService {
	
	private final PushMessageRepository msgRepo;
	private final PushInboxRepository inboxRepo;
	
	private final InboxSseHub inboxSseHub;
	
	/**
	 * 인앱 알림 저장 + SSE 브로드캐스트
	 * - PushMessage 저장
	 * - PushInbox 저장
	 * - 화면 켜진 사용자에게 SSE 즉시 전송
	 */
	@Transactional
	public void notifyInboxAndAfterCommit(
								String eventType,
								List<String> targets,
								String title,
								String body,
								String linkUrl,
								String payloadJson,
								String createdByEmpNo,
								Runnable afterCommit
							) {
		
		if (eventType == null || eventType.isBlank()) return;
		if (title == null || title.isBlank()) return;
		if (targets  == null || targets.isEmpty()) return;
			
		// 1) 메시지 원본 저장
		PushMessage msg = msgRepo.save(
				PushMessage.of(
						eventType, 
						title, 
						body, 
						linkUrl, 
						payloadJson, 
						createdByEmpNo
						)
				);
		
		// 2) 인박스 저장
		List<PushInbox> rows = targets.stream()
			    .distinct()
			    .map(empNo -> PushInbox.of(empNo, msg.getId()))
			    .toList();
		
		if (!rows.isEmpty()) {
			inboxRepo.saveAllInbox(rows);
		}
		
		// 3) 커밋 후 작업 실행 (SSE/WebPush)
		if (afterCommit != null && TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(
					new TransactionSynchronization() {
						@Override
						public void afterCommit() {
						    afterCommit.run();
						    inboxSseHub.broadcastInbox(rows, msg);
						}
					}
				);
		}
	}
}
