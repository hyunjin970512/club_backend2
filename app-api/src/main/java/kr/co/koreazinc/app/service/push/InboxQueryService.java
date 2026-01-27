package kr.co.koreazinc.app.service.push;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.push.InboxItemDto;
import kr.co.koreazinc.temp.model.entity.push.PushInbox;
import kr.co.koreazinc.temp.repository.push.PushInboxRepository;

@Service
@RequiredArgsConstructor
public class InboxQueryService {

	private final PushInboxRepository inboxRepo;
	
	/**
	 * 알림 목록 조회
	 */
	@Transactional(readOnly = true)
	public List<InboxItemDto> list(String empNo, int size) {
	
		return inboxRepo
				.selectInboxList(InboxItemDto.class)
				.eqEmpNo(empNo)
				.orderLatest()
				.limit(size)
				.fetch();
	}
	
	/**
	 * 읽음 처리
	 */
	@Transactional
	public void markRead(String empNo, Long inboxId) {
	
		PushInbox inbox = inboxRepo.findOneByIdAndEmpNo(inboxId, empNo);
		if (inbox == null) {
			throw new IllegalArgumentException("Inbox not found or not allowed");
		}
		
		inbox.markRead();
	}
	
	/**
	 * 읽지 않은 알림 수
	 */
	@Transactional(readOnly = true)
	public long countUnread(String empNo) {
		return inboxRepo.countUnread(empNo);
	}
}
