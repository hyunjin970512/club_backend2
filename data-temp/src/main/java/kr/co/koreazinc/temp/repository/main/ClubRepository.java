package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class ClubRepository extends AbstractJpaRepository<ClubUserInfo, Long> {

    private static final String JOINED_STATUS = "20"; // ✅ 가입 상태코드

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

        // 동호회 가입 상태코드 co_common_code > CLUB_USER_STATUS_CD >20
        public SelectQuery<DTO> eqStatus(String status) {
            query.where(cui.status.eq(status));
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
     * - AND   cui.status = '20'
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
                .join(ci).on(ccr.clubId.eq(ci.clubId))
                .join(cui).on(ci.clubId.eq(cui.clubId))
        ).joinedOnly(); // ✅ status='20' 기본 적용
    }
}
