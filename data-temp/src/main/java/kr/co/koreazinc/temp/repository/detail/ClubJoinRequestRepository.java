package kr.co.koreazinc.temp.repository.detail;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;

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
	            clubJoinRequest.applyReason,
	            Expressions.stringTemplate("TO_CHAR({0}, {1})", 
	            		clubJoinRequest.requestDate, "YYYY-MM-DD").as("requestDate"),
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
     * 가입 요청 상태 업데이트 (승인/거절)
     */
	public long updateJoinRequestStatus(Long clubId, String requestEmpNo, String status, String updateUser) {
        return queryFactory.update(clubJoinRequest)
        		.set(clubJoinRequest.status, status)
        		.set(clubJoinRequest.updateUser, updateUser)
        		.set(clubJoinRequest.updateDate, LocalDateTime.now())
                .where(
                    clubJoinRequest.clubId.eq(clubId),
                    clubJoinRequest.requestUser.eq(requestEmpNo)
                )
                .execute();
    }
	
	/**
     * 승인 시 멤버 추가
     */
	@Transactional
	public void insertClubMember(Long clubId, String empNo, String createUser) {
		ClubUserInfo newUser = ClubUserInfo.builder()
				.clubId(clubId)
				.empNo(empNo)
				.userRoleCd("10")
				.status("10")
				.createUser(createUser)
				.build();
		
		entityManager.persist(newUser);
	}
}
