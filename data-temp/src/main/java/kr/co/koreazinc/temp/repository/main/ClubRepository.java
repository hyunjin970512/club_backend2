package kr.co.koreazinc.temp.repository.main;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.account.QCoEmplBas;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class ClubRepository extends AbstractJpaRepository<ClubUserInfo, Long> {

    private static final String JOINED_STATUS = "10"; // ✅ 가입 상태코드

    public ClubRepository(List<EntityManager> entityManagers) {
        super(ClubUserInfo.class, entityManagers);
    }

    /** 체이닝용 래퍼 (MenuRepository 스타일) */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        private final QClubInfo ci = QClubInfo.clubInfo;
        private final QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> eqEmpNo(String empNo) {
            query.where(cui.empNo.eq(empNo));
            return this;
        }

        // 동호회 가입 상태코드 co_common_code > CLUB_USER_STATUS_CD > 10
        public SelectQuery<DTO> eqStatus(String status) {
            query.where(cui.status.eq(status));
            return this;
        }
        
        /** 동호회 ID 조건 */
        public SelectQuery<DTO> eqClubId(Integer clubId) {
            query.where(cui.clubId.eq(clubId.longValue()));
            return this;
        }

        /** 가입 상태 기본 조건 */
        public SelectQuery<DTO> joinedOnly() {
            return eqStatus(JOINED_STATUS);
        }

        public SelectQuery<DTO> orderDefault() {
            query.orderBy(ci.clubNm.asc(), ci.clubId.asc());
            return this;
        }
    }

    /**
     * 가입한 동호회 조회
     * - WHERE cui.emp_no = ?
     * - AND   cui.status = '10'
     */
    public <T> SelectQuery<T> selectJoinedClubs(Class<T> type) {

        QClubCreateRequest ccr = QClubCreateRequest.clubCreateRequest;
        QClubInfo ci = QClubInfo.clubInfo;
        QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    ccr.clubId,     // clubId
                    ci.clubNm,      // clubNm
                    ccr.clubDesc    // clubDesc
                ))
                .from(ccr)
                .join(ci).on(
                		ccr.clubId.eq(ci.clubId),
                		ci.status.notIn("40", "50")
                		)
                .join(cui).on(ci.clubId.eq(cui.clubId))
        ).joinedOnly(); // ✅ status='10' 기본 적용
    }
    
    /**
     * 가입 요청한 동호회 조회
     * - WHERE cui.emp_no = ?
     * - AND   cui.status = '10'
     */
    public List<Long> selectJoinRequestClubIds(String empNo) {
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;
        QClubInfo ci = QClubInfo.clubInfo;
        
        return queryFactory
            .select(cjr.clubId) // ID 하나만 조회
            .from(cjr)
            .join(ci).on(
                    cjr.clubId.eq(ci.clubId),
                    ci.status.notIn("40", "50")
                )
            .where(
                    cjr.status.eq("10"),
                    cjr.requestUser.eq(empNo)
                )
            .fetch();
    }
    
    /**
     * 동호회 멤버 목록 조회
     */
    public <T> List<T> selectClubMembers(Class<T> type, Integer clubId) {
    	QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    	QCoEmplBas ceb = QCoEmplBas.coEmplBas;
    	
    	return queryFactory
    	        .select(Projections.bean(type,
    	            cui.empNo.as("memberEmpNo"),
    	            cui.joinDate.as("joinDate"),
    	            ceb.deptCd.as("deptNm"),
    	            ceb.nameKo.as("memberNm"),
    	            ceb.positionCd.as("positionCd"),
    	            ceb.empNo,
    	            Expressions.stringTemplate("fn_get_common_code({0}, {1})", 
    	                    "COMPANY_CD", 
    	                    ceb.coCd
    	                ).as("companyNm")
    	        ))
    	        .from(cui)
    	        .leftJoin(ceb).on(ceb.empNo.eq(cui.empNo))
    	        .where(
    	            cui.clubId.eq(clubId.longValue()),
    	            cui.status.eq("10")
    	        )
    	        .orderBy(ceb.nameKo.asc())
    	        .fetch();
    }
    
    
    /**
     * 동호회 멤버 목록 제거
     */
    @Transactional
    public long deleteClubMembers(Long clubId, List<String> memberEmpNos, String updateUser) {
    	QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    	
    	return queryFactory.update(cui)
                .set(cui.status, "20")
                .set(cui.updateUser, updateUser)
                .set(cui.updateDate, LocalDateTime.now())
                .where(
                    cui.clubId.eq(clubId),
                    cui.empNo.in(memberEmpNos)
                )
                .execute();
    }
    
    /**
     * 동호회 탈퇴
     */
    @Transactional
    public long quitClub(Integer clubId, String empNo) {
    	QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;
    	QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    	
    	// 가입 요청 테이블 상태 변경
    	queryFactory.update(cjr)
    		.set(cjr.status, "40")
    		.set(cjr.updateUser, empNo)
    		.set(cjr.updateDate, LocalDateTime.now())
    		.where(
    				cjr.clubId.eq(clubId.longValue()),
    				cjr.requestUser.eq(empNo)
    		)
    		.execute();
    	
    	// 멤버 정보 테이블 상태 변경
    	return queryFactory.update(cui)
                .set(cui.status, "20")
                .set(cui.updateUser, empNo)
                .set(cui.updateDate, LocalDateTime.now())
                .where(
                    cui.clubId.eq(clubId.longValue()),
                    cui.empNo.eq(empNo)
                )
                .execute();
    }
}
