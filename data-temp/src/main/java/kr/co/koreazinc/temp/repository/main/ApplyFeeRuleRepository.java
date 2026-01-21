package kr.co.koreazinc.temp.repository.main;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubApplyFeeRuleDetail;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleBas;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleDetail;


import com.querydsl.core.types.dsl.DateTemplate;


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

        public SelectQuery<DTO> orderByLineId() {
            query.orderBy(cafrd.lineId.asc());
            return this;
        }
    }

    /**
     * 현재 적용 중인 규정 상세 조회
     */
    public <T> SelectQuery<T> selectCurrentRuleDetails(Class<T> type) {

        QClubApplyFeeRuleDetail cafrd =
                QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;
        QClubApplyFeeRuleBas cafrb =
                QClubApplyFeeRuleBas.clubApplyFeeRuleBas;

        DateTemplate<LocalDate> today = Expressions.dateTemplate(LocalDate.class, "current_date");

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
                                today.between(cafrb.applyStartDt, cafrb.applyEndDt)
                            )
                            .orderBy(cafrb.applyId.desc())
                            .limit(1)
                    )
                )
        );
    }
}
