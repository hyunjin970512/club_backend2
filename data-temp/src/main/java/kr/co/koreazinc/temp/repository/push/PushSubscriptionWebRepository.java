package kr.co.koreazinc.temp.repository.push;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionWeb;
import kr.co.koreazinc.temp.model.entity.push.QPushSubscriptionWeb;

@Repository
@Transactional(readOnly = true)
public class PushSubscriptionWebRepository extends AbstractJpaRepository<PushSubscriptionWeb, Long> {

	public PushSubscriptionWebRepository(List<EntityManager> entityManagers) {
		super(PushSubscriptionWeb.class, entityManagers);
	}
	
	/* =========================
	 * 체이닝용 SelectQuery
	 * ========================= */
	public class SelectQuery<DTO> extends Query.Select<DTO> {
	
		private final QPushSubscriptionWeb ps = QPushSubscriptionWeb.pushSubscriptionWeb;
			
		public SelectQuery(JPAQuery<DTO> query) {
			super(query);
		}
			
		public SelectQuery<DTO> eqEmpNo(String empNo) {
			query.where(ps.empNo.eq(empNo));
			return this;
		}
			
		public SelectQuery<DTO> eqEndpoint(String endpoint) {
			query.where(ps.endpoint.eq(endpoint));
			return this;
		}
			
		public SelectQuery<DTO> activeOnly() {
			query.where(ps.activeYn.eq(PushSubscriptionWeb.Y));
			return this;
		}
	}
	
	/* =========================
	 * endpoint 단건 조회
	 * ========================= */
	public Optional<PushSubscriptionWeb> findOneByEndpoint(String endpoint) {
	    QPushSubscriptionWeb ps = QPushSubscriptionWeb.pushSubscriptionWeb;
	
	    return Optional.ofNullable(
			queryFactory
				.selectFrom(ps)
				.where(ps.endpoint.eq(endpoint))
				.fetchOne()
	    );
	}
	
	/* =========================
	 * empNo + endpoint 단건 조회
	 * ========================= */
	public Optional<PushSubscriptionWeb> findOneByEmpNoAndEndpoint(
																	String empNo,
																	String endpoint
																	) {
		QPushSubscriptionWeb ps = QPushSubscriptionWeb.pushSubscriptionWeb;
			
		return Optional.ofNullable(
									queryFactory
										.selectFrom(ps)
										.where(
												ps.empNo.eq(empNo),
												ps.endpoint.eq(endpoint)
										)
										.fetchOne()
		);
	}
	
	/* =========================
	 * WebPush 발송용
	 * ========================= */
	public List<PushSubscriptionWeb> findActiveByEmpNo(String empNo) {
		QPushSubscriptionWeb ps = QPushSubscriptionWeb.pushSubscriptionWeb;
			
		return queryFactory
					.selectFrom(ps)
					.where(
							ps.empNo.eq(empNo),
							ps.activeYn.eq(PushSubscriptionWeb.Y)
					)
					.fetch();
	}
}
