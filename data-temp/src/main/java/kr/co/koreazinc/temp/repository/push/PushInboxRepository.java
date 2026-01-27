package kr.co.koreazinc.temp.repository.push;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.push.PushInbox;
import kr.co.koreazinc.temp.model.entity.push.QPushInbox;
import kr.co.koreazinc.temp.model.entity.push.QPushMessage;

@Repository
@Transactional(readOnly = true)
public class PushInboxRepository extends AbstractJpaRepository<PushInbox, Long> {

	private final EntityManager em;
	
	public PushInboxRepository(List<EntityManager> entityManagers) {
		super(PushInbox.class, entityManagers);
		this.em = entityManagers.get(0);
	}
	
	/* =========================
	 * 체이닝용 SelectQuery
	 * ========================= */
	public class SelectQuery<DTO> extends Query.Select<DTO> {
	
		private final QPushInbox pi = QPushInbox.pushInbox;
		private final QPushMessage pm = QPushMessage.pushMessage;
			
		public SelectQuery(JPAQuery<DTO> query) {
			super(query);
		}
			
		public SelectQuery<DTO> eqEmpNo(String empNo) {
			query.where(pi.empNo.eq(empNo));
			return this;
		}
			
		public SelectQuery<DTO> unreadOnly() {
			query.where(pi.readYn.eq(PushInbox.READ_N));
			return this;
		}
			
		public SelectQuery<DTO> orderLatest() {
			query.orderBy(pi.createdAt.desc(), pi.id.desc());
			return this;
		}
			
		public SelectQuery<DTO> limit(int size) {
			query.limit(size);
			return this;
		}
	}
	
	/* =========================
	 * 알림 목록 조회
	 * ========================= */
	public <T> SelectQuery<T> selectInboxList(Class<T> type) {
	
		QPushInbox pi = QPushInbox.pushInbox;
		QPushMessage pm = QPushMessage.pushMessage;
			
		return new SelectQuery<>(
			queryFactory
				.select(Projections.constructor(
					type,
					pi.id,          // inboxId
					pi.readYn,      // readYn
					pi.createdAt,   // createdAt
					pm.eventType,
					pm.title,
					pm.body,
					pm.linkUrl,
					pm.payloadJson
				))
				.from(pi)
				.join(pm).on(pi.messageId.eq(pm.id))
		);
	}
	
	/* =========================
	 * 단건 조회 (읽음 처리)
	 * ========================= */
	public PushInbox findOneByIdAndEmpNo(Long inboxId, String empNo) {
	
		QPushInbox pi = QPushInbox.pushInbox;
			
		return queryFactory
				.selectFrom(pi)
				.where(
						pi.id.eq(inboxId),
						pi.empNo.eq(empNo)
				)
				.fetchOne();
	}
	
	/* =========================
	 * 읽지 않은 알림 수
	 * ========================= */
	public long countUnread(String empNo) {
	
		QPushInbox pi = QPushInbox.pushInbox;
			
		return queryFactory
				.select(pi.count())
				.from(pi)
				.where(
						pi.empNo.eq(empNo),
						pi.readYn.eq("N")
				)
				.fetchOne();
	}
	
	@Transactional
	public void saveAllInbox(List<PushInbox> rows) {
		
		if (rows == null || rows.isEmpty()) return;

		int i = 0;
		for (PushInbox row : rows) {
		    entityManager.persist(row);
		    i++;
		    if (i % 50 == 0) {
		    	em.flush();
		    	em.clear();
		    }
		}
	}
}
