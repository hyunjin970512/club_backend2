package kr.co.koreazinc.temp.repository.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.admin.SubsidyRuleCommandRow;
import kr.co.koreazinc.temp.model.entity.main.ClubApplyFeeRuleBas;
import kr.co.koreazinc.temp.model.entity.main.ClubApplyFeeRuleDetail;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleBas;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleDetail;

@Repository
public class SubsidyRuleCommandRepository
        extends AbstractJpaRepository<ClubApplyFeeRuleBas, Long> {

    private final EntityManager em;

    public SubsidyRuleCommandRepository(List<EntityManager> entityManagers) {
        super(ClubApplyFeeRuleBas.class, entityManagers);
        this.em = entityManagers.get(0); // 회사 표준: 첫 EM
    }

    /**
     * ✅ 현재(오늘) 적용중인 규정이 있으면 전부 use_yn='N'
     * - 조건: use_yn='Y' AND today between start~end (end null 허용)
     */
    @Transactional
    public long deactivateCurrentRules(String empNo) {

        QClubApplyFeeRuleBas bas = QClubApplyFeeRuleBas.clubApplyFeeRuleBas;

        DateTemplate<LocalDate> today =
                Expressions.dateTemplate(LocalDate.class, "current_date");

        LocalDateTime now = LocalDateTime.now();

        return queryFactory
                .update(bas)
                .set(bas.useYn, "N")
                .set(bas.updateUser, empNo)
                .set(bas.updateDate, now)
                .where(
                        bas.useYn.eq("Y"),
                        bas.applyStartDt.loe(today),
                        bas.applyEndDt.isNull().or(bas.applyEndDt.goe(today))
                )
                .execute();
    }

    /**
     * ✅ BAS 신규 생성 (use_yn='Y') + apply_id 확실히 리턴
     * - PK가 GENERATED ALWAYS IDENTITY라서 apply_id는 절대 넣으면 안 됨
     * - Postgres는 returning으로 키 받는 게 제일 확실함
     */
    @Transactional
    public Long insertBas(LocalDate applyStartDt, LocalDate applyEndDt, String empNo) {

        Object raw = em.createNativeQuery("""
            insert into club_apply_fee_rule_bas
              (apply_start_dt, apply_end_dt, use_yn, create_user, create_date)
            values
              (:s, :e, 'Y', :u, now())
            returning apply_id
        """)
        .setParameter("s", applyStartDt)
        .setParameter("e", applyEndDt)
        .setParameter("u", empNo)
        .getSingleResult();

        Long applyId = (raw instanceof Number n) ? n.longValue() : null;
        if (applyId == null) {
            throw new IllegalStateException("applyId is null after insertBas()");
        }

        return applyId;
    }

    /**
     * ✅ DETAIL 벌크 생성 (⚠️ 연관관계 bas로 저장해야 apply_id가 들어감)
     */
    @Transactional
    public void insertDetails(Long applyId, List<SubsidyRuleCommandRow> details, String empNo) {

        if (applyId == null) throw new IllegalArgumentException("applyId is null");
        if (details == null || details.isEmpty()) return;

        // ✅ 핵심: FK는 bas(연관객체)로 저장
        ClubApplyFeeRuleBas basRef = em.getReference(ClubApplyFeeRuleBas.class, applyId);

        LocalDateTime now = LocalDateTime.now();

        int i = 0;
        for (SubsidyRuleCommandRow d : details) {

            // 서버 방어
            if (d == null) continue;
            if (d.memberCntFrom() == null || d.memberCntFrom() <= 0) continue;
            if (d.payAmount() == null || d.payAmount() <= 0) continue;
            if (d.memberCntTo() != null && d.memberCntTo() < d.memberCntFrom()) continue;

            ClubApplyFeeRuleDetail det = new ClubApplyFeeRuleDetail();
            det.setBas(basRef); // ✅ 여기로 apply_id 들어간다 (det.applyId는 읽기전용임)
            det.setMemberCntFrom(d.memberCntFrom());
            det.setMemberCntTo(d.memberCntTo());
            det.setPayAmount(d.payAmount());
            det.setCreateUser(empNo);
            det.setCreateDate(now);

            em.persist(det);

            if (++i % 50 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.flush();
    }

    /**
     * ✅ (선택) 같은 applyId detail 싹 지우고 다시 넣고 싶을 때
     * - applyId 필드는 읽기용(insert/update 불가)이지만 where 조건에는 사용 가능
     */
    @Transactional
    public long deleteDetailsByApplyId(Long applyId) {

        QClubApplyFeeRuleDetail det = QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;

        return queryFactory
                .delete(det)
                .where(det.applyId.eq(applyId))
                .execute();
    }

}
