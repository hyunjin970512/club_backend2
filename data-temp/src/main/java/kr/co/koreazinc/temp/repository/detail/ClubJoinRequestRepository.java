package kr.co.koreazinc.temp.repository.detail;

import java.util.List;
import java.util.Optional;

import static kr.co.koreazinc.temp.model.entity.account.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest.clubJoinRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.main.ClubJoinRequest;

@Repository
@Transactional(readOnly = true)
public class ClubJoinRequestRepository extends AbstractJpaRepository<ClubJoinRequest, Long> {
	public ClubJoinRequestRepository(List<EntityManager> entityManagers) {
        super(ClubJoinRequest.class, entityManagers);
    }
	
	/**
     * 가입 요청 리스트 조회
     */
	public <T> List<T> findRequestList(Class<T> type, Integer clubId) {
	    return queryFactory
	        .select(Projections.bean(type,
	            clubJoinRequest.requestUser.as("requestEmpNo"),
	            coEmplBas.nameKo.as("requestNm"),
	            coEmplBas.deptCd.as("deptNm"),
	            coEmplBas.positionCd.as("positionCd"),
	            Expressions.stringTemplate("fn_get_common_code({0}, {1})", 
	                    "COMPANY_CD", 
	                    coEmplBas.coCd
	                ).as("companyNm")
	        ))
	        .from(clubJoinRequest)
	        .leftJoin(coEmplBas).on(coEmplBas.empNo.eq(clubJoinRequest.requestUser))
	        .where(
	            clubJoinRequest.clubId.eq(clubId.longValue()),
	            clubJoinRequest.status.eq("10")
	        )
	        .fetch();
	}
	
	/**
     * 승인/거절 처리를 위해 단건 엔티티 조회
     */
	public Optional<ClubJoinRequest> findByClubIdAndRequestUser(Long clubId, String requestUser) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(clubJoinRequest)
                .where(
                    clubJoinRequest.clubId.eq(clubId),
                    clubJoinRequest.requestUser.eq(requestUser)
                )
                .fetchOne()
        );
    }
}
