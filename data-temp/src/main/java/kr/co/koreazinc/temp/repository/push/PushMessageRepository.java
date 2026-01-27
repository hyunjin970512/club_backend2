package kr.co.koreazinc.temp.repository.push;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.push.PushMessage;
import kr.co.koreazinc.temp.model.entity.push.QPushMessage;

@Repository
@Transactional(readOnly = true)
public class PushMessageRepository extends AbstractJpaRepository<PushMessage, Long> {

	public PushMessageRepository(List<EntityManager> entityManagers) {
		super(PushMessage.class, entityManagers);
	}
	
	/* =========================
	 * 단건 조회
	 * ========================= */
	public Optional<PushMessage> findOneById(Long id) {
		if (id == null) return Optional.empty();
			
		QPushMessage pm = QPushMessage.pushMessage;
			
		return Optional.ofNullable(
			queryFactory
				.selectFrom(pm)
				.where(pm.id.eq(id))
				.fetchOne()
		);
	}
}
