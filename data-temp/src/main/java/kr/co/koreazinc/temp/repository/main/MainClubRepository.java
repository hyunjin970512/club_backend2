package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QCoCommonCode;

@Repository
@Transactional(readOnly = true)
public class MainClubRepository extends AbstractJpaRepository<ClubInfo, Long> {

    private static final String MAIN_CODE_CLUB_TYPE = "CLUB_TYPE";
    
    // 동호회 가입 상태 코드
    private static final String JOINED_STATUS = "10";
    
    // 동호회 상태코드
    private static final String STATUS_10 = "10";	//사전요청
    private static final String STATUS_20 = "20";	//정식요청
    private static final String STATUS_30 = "30";	//운영중


    public MainClubRepository(List<EntityManager> entityManagers) {
        super(ClubInfo.class, entityManagers);
    }

    /** ✅ 체이닝용 래퍼 */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        private final QClubInfo ci = QClubInfo.clubInfo;
        private final QCoCommonCode ccc = QCoCommonCode.coCommonCode;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        /**
         * ✅ area(칩) 필터
         * - null/blank/ALL/전체면 필터 없음
         * - area가 sub_code로 오면: ci.clubType = area
         * - area가 code_nm으로 오면: ccc.codeNm = area (조인된 공통코드명 기준)
         */
        public SelectQuery<DTO> filterArea(String area) {
            if (area == null) return this;
            String a = area.trim();
            if (a.isBlank()) return this;
            if ("ALL".equalsIgnoreCase(a) || "전체".equals(a)) return this;

            query.where(
                ci.clubType.eq(a)
                    .or(ccc.codeNm.eq(a))
            );
            return this;
        }

        public SelectQuery<DTO> orderDefault() {
            query.orderBy(ci.clubId.asc());
            return this;
        }
    }

    /**
     * 동호회 목록 조회
     * - club_type명(code_nm)
     * - 가입자수 (status='10') join_cnt
     */
    public <T> SelectQuery<T> selectClubList(Class<T> type) {

        QClubInfo ci = QClubInfo.clubInfo;
        QClubCreateRequest ccr = QClubCreateRequest.clubCreateRequest;
        QCoCommonCode ccc = QCoCommonCode.coCommonCode;

        QClubUserInfo cuiSub = new QClubUserInfo("cuiSub");
        var joinCnt = JPAExpressions
            .select(cuiSub.clubUserId.count())
            .from(cuiSub)
            .where(
                cuiSub.clubId.eq(ci.clubId),
                cuiSub.status.eq(JOINED_STATUS)
            );

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    ci.clubId,
                    ci.clubNm,
                    ccr.clubDesc,
                    ci.clubType,
                    ccc.codeNm,
                    joinCnt
                ))
                .from(ci)
                .join(ccr).on(ci.clubId.eq(ccr.clubId))
                .join(ccc).on(
                    ccc.id.mainCode.eq(MAIN_CODE_CLUB_TYPE)
                        .and(ci.clubType.eq(ccc.id.subCode))
                )
                .where(
                        ci.status.in(STATUS_10, STATUS_20, STATUS_30)
                    )
        );
    }

    /** ✅ 편의 메서드: area까지 받아서 바로 리스트 */
    public <T> List<T> findClubList(Class<T> type, String area) {
        return selectClubList(type)
            .filterArea(area)
            .orderDefault()
            .fetch();
    }
}
