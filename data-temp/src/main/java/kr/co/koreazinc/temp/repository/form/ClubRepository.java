package kr.co.koreazinc.temp.repository.form;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class ClubRepository extends AbstractJpaRepository<ClubUserInfo, Long> {

    private static final String JOINED_STATUS_10 = "10"; // 가입 상태코드 - 신청
    private static final String JOINED_STATUS_20 = "20"; // 가입 상태코드 - 승인

    public ClubRepository(List<EntityManager> entityManagers) {
        super(ClubUserInfo.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {
        private final QClubInfo ci = QClubInfo.clubInfo;
        private final QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        public SelectQuery(JPAQuery<DTO> query) { super(query); }

        public SelectQuery<DTO> orderDefault() {
            query.orderBy(ci.clubNm.asc(), ci.clubId.asc());
            return this;
        }
    }
    
    public boolean existsJoined(String empNo, Long clubId) {
        QClubUserInfo cui = QClubUserInfo.clubUserInfo;
        Integer one = queryFactory
            .selectOne()
            .from(cui)
            .where(
                cui.empNo.eq(empNo),
                cui.clubId.eq(clubId),
                cui.status.eq("20") // 가입
            )
            .fetchFirst();
        return one != null;
    }


    /**
     * ✅ 동호회 단건 조회 (가입신청 화면 동호회명용)
     * 프론트: GET /api/clubs/{clubId}
     * DTO 생성자: (Long clubId, String clubNm)
     */
    public <T> T selectClubSimple(Class<T> type, Long clubId) {
        QClubInfo ci = QClubInfo.clubInfo;

        return queryFactory
            .select(Projections.fields(
                type,
                ci.clubId.as("clubId"),
                ci.clubNm.as("clubNm")
            ))
            .from(ci)
            .where(ci.clubId.eq(clubId))
            .fetchOne();
    }

    /**
     * 가입 가능한 동호회 조회
     * - club_info 기준
     * - 내가 이미 가입(status=10/20)한 club 제외
     * - 내가 이미 가입신청(club_join_request 존재)한 club 제외
     */
    public <T> SelectQuery<T> selectJoinableClubs(Class<T> type, String empNo) {

        QClubInfo ci = QClubInfo.clubInfo;
        QClubUserInfo cui = QClubUserInfo.clubUserInfo;
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    ci.clubId,
                    ci.clubNm
                ))
                .from(ci)
                // ✅ 이미 가입/신청 상태로 club_user_info 있으면 제외
                .leftJoin(cui).on(
                    cui.clubId.eq(ci.clubId)
                        .and(cui.empNo.eq(empNo))
                        .and(cui.status.in(JOINED_STATUS_10, JOINED_STATUS_20))
                )
                // ✅ 이미 신청(club_join_request) 있으면 제외
                .leftJoin(cjr).on(
                    cjr.clubId.eq(ci.clubId)
                        .and(cjr.requestUser.eq(empNo))
                )
                .where(cui.clubId.isNull())
                .where(cjr.requestId.isNull())
        ).orderDefault();
    }
}
