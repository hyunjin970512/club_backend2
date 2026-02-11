package kr.co.koreazinc.temp.repository.form;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubJoinRequest;
import kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest;

@Repository
@Transactional(readOnly = true)
public class ClubJoinRequestRepository extends AbstractJpaRepository<ClubJoinRequest, Long> {

    public ClubJoinRequestRepository(List<EntityManager> entityManagers) {
        super(ClubJoinRequest.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {
        public SelectQuery(JPAQuery<DTO> query) { super(query); }
    }

    /**
     * ✅ 내 "활성" 신청/가입(10=신청, 20=승인) request_id 조회
     * - 신청중이거나 이미 승인(가입중)인 상태면 존재
     * - 반려(30), 취소(40)는 제외 → 재신청 허용
     */
    public Long findActiveRequestIdMineByClubId(String empNo, Long clubId) {
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;

        return queryFactory
            .select(cjr.requestId)
            .from(cjr)
            .where(
                cjr.requestUser.eq(empNo),
                cjr.clubId.eq(clubId),
                cjr.status.in("10", "20")
            )
            .fetchFirst();
    }

    /**
     * ✅ 활성 상태(신청/승인)만 중복 체크
     * - 재가입(반려/취소 후 재신청)은 허용되어야 하므로 전체 이력 카운트 금지
     */
    public long countActiveByClubIdAndRequestUser(Long clubId, String empNo) {
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;

        Long cnt = queryFactory
            .select(cjr.requestId.count())
            .from(cjr)
            .where(
                cjr.clubId.eq(clubId),
                cjr.requestUser.eq(empNo),
                cjr.status.in("10", "20")
            )
            .fetchOne();

        return cnt == null ? 0L : cnt;
    }

    /**
     * ✅ requestId로 단건 조회 + 본인 것만
     * (상태 제한 없음: 신청/승인/반려/취소 전부 조회 가능)
     */
    public ClubJoinRequest findOneMine(Long requestId, String empNo) {
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;

        return queryFactory
            .selectFrom(cjr)
            .where(
                cjr.requestId.eq(requestId),
                cjr.requestUser.eq(empNo)
            )
            .fetchOne();
    }

    /**
     * ✅ request 단건 DTO 조회 (필요하면 사용)
     * DTO 생성자: (Long requestId, Long clubId, String applyReason, String status, String requestUser, LocalDateTime requestDate)
     */
    public <T> T selectRequestDto(Class<T> type, Long requestId, String empNo) {
        QClubJoinRequest cjr = QClubJoinRequest.clubJoinRequest;

        return queryFactory
            .select(Projections.constructor(
                type,
                cjr.requestId,
                cjr.clubId,
                cjr.applyReason,
                cjr.status,
                cjr.requestUser,
                cjr.requestDate
            ))
            .from(cjr)
            .where(
                cjr.requestId.eq(requestId),
                cjr.requestUser.eq(empNo)
            )
            .fetchOne();
    }
}
