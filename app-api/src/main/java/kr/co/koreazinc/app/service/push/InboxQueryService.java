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
	public List<InboxItemDto> list(String empNo, int size, Boolean unreadOnly) {
	    var q = inboxRepo
	            .selectInboxList(InboxItemDto.class)
	            .eqEmpNo(empNo)
	            .orderLatest()
	            .limit(size);

	    if (Boolean.TRUE.equals(unreadOnly)) q.unreadOnly();

	    return q.fetch();
	}
	
	/**
	 * 읽음 처리
	 */
	@Transactional
	public void markRead(String empNo, Long inboxId) {

	    long updated = inboxRepo.markAsRead(inboxId, empNo);
	}
	
	/**
	 * 전체 읽음 처리
	 */
	@Transactional
	public int markAllRead(String empNo) {
	  return (int) inboxRepo.markAllAsRead(empNo);
	}
	
	/**
	 * 읽지 않은 알림 수
	 */
	@Transactional(readOnly = true)
	public long countUnread(String empNo) {
		return inboxRepo.countUnread(empNo);
	}
}
