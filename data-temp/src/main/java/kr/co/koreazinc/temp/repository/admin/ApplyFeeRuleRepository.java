package kr.co.koreazinc.temp.repository.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubApplyFeeRuleDetail;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleBas;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleDetail;

@Repository
@Transactional(readOnly = true)
public class ApplyFeeRuleRepository
        extends AbstractJpaRepository<ClubApplyFeeRuleDetail, Long> {

    public ApplyFeeRuleRepository(List<EntityManager> entityManagers) {
        super(ClubApplyFeeRuleDetail.class, entityManagers);
    }

    /** 체이닝용 */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        private final QClubApplyFeeRuleDetail cafrd =
                QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        /** ✅ 니가 원래 쓰던 이름 */
        public SelectQuery<DTO> orderByLineId() {
            query.orderBy(cafrd.lineId.asc());
            return this;
        }

        /** ✅ 내가 서비스에서 쓰던 이름(추가) */
        public SelectQuery<DTO> orderByLineIdAsc() {
            return orderByLineId();
        }
    }

    /** ✅ 이력 조회 */
    public <T> SelectQuery<T> selectHistory(Class<T> type) {
        QClubApplyFeeRuleBas cafrb = QClubApplyFeeRuleBas.clubApplyFeeRuleBas;

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    cafrb.applyId,
                    cafrb.applyStartDt,
                    cafrb.applyEndDt,
                    cafrb.useYn
                ))
                .from(cafrb)
                .where(cafrb.useYn.eq("Y"))
                .orderBy(cafrb.applyStartDt.desc().nullsLast(), cafrb.applyId.desc())
        );
    }

    /** ✅ 특정 applyId 상세 조회(이력에서 선택했을 때) */
    public <T> SelectQuery<T> selectRuleDetailsByApplyId(Class<T> type, Long applyId) {

        QClubApplyFeeRuleDetail cafrd = QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    cafrd.lineId,
                    cafrd.applyId,
                    cafrd.memberCntFrom,
                    cafrd.memberCntTo,
                    cafrd.payAmount
                ))
                .from(cafrd)
                .where(cafrd.applyId.eq(applyId))
        );
    }

    /** ✅ 현재 적용중 상세 조회 (니가 올린 버전 그대로 유지, end null 대응 포함) */
    public <T> SelectQuery<T> selectCurrentRuleDetails(Class<T> type) {

        QClubApplyFeeRuleDetail cafrd = QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;
        QClubApplyFeeRuleBas cafrb = QClubApplyFeeRuleBas.clubApplyFeeRuleBas;

        DateTemplate<LocalDate> today =
                Expressions.dateTemplate(LocalDate.class, "current_date");

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    cafrd.lineId,
                    cafrd.applyId,
                    cafrd.memberCntFrom,
                    cafrd.memberCntTo,
                    cafrd.payAmount
                ))
                .from(cafrd)
                .where(
                    cafrd.applyId.eq(
                        JPAExpressions
                            .select(cafrb.applyId)
                            .from(cafrb)
                            .where(
                                cafrb.useYn.eq("Y"),
                                cafrb.applyStartDt.loe(today),
                                cafrb.applyEndDt.isNull().or(cafrb.applyEndDt.goe(today))
                            )
                            .orderBy(cafrb.applyId.desc())
                            .limit(1)
                    )
                )
        );
    }
}
