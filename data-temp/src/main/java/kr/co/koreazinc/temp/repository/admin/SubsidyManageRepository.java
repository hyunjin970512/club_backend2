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
import kr.co.koreazinc.temp.model.entity.account.QCoEmplBas;
import kr.co.koreazinc.temp.model.entity.admin.ClubApplyFeeManage;
import kr.co.koreazinc.temp.model.entity.admin.QClubApplyFeeManage;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleBas;
import kr.co.koreazinc.temp.model.entity.main.QClubApplyFeeRuleDetail;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class SubsidyManageRepository
    extends AbstractJpaRepository<ClubApplyFeeManage, Long> {

  public SubsidyManageRepository(List<EntityManager> entityManagers) {
    super(ClubApplyFeeManage.class, entityManagers);
  }

  /** 체이닝용 */
  public class SelectQuery<DTO> extends Query.Select<DTO> {
    public SelectQuery(JPAQuery<DTO> query) { super(query); }
  }

  /** ✅ 해당년도 manage 데이터 존재 여부 */
  public boolean existsManageByYear(String year) {
    QClubApplyFeeManage m = QClubApplyFeeManage.clubApplyFeeManage;

    Integer one = queryFactory
        .selectOne()
        .from(m)
        .where(m.year.eq(year))
        .fetchFirst();

    return one != null;
  }

  /** ✅ manage 테이블(있을 때) 조회 */
  public <T> SelectQuery<T> selectManageRows(Class<T> type, String year) {
    QClubApplyFeeManage m = QClubApplyFeeManage.clubApplyFeeManage;
    QClubInfo ci = QClubInfo.clubInfo;
    QCoEmplBas ceb = QCoEmplBas.coEmplBas;

    var leaderExpr = Expressions.stringTemplate(
        "coalesce({0} || ' ' || {1}, '미상')",
        ceb.nameKo, ceb.positionCd
    );

    return new SelectQuery<>(
        queryFactory
            .select(Projections.constructor(
                type,
                m.manageId,
                m.clubId,
                ci.clubNm,
                leaderExpr,
                m.clubMemberCnt,
                m.supportAmount,
                m.payYn
            ))
            .from(m)
            .join(ci).on(ci.clubId.eq(m.clubId))
            .leftJoin(ceb).on(
                ceb.empNo.eq(ci.clubMasterId)
                    .and(ceb.deleteAt.ne("Y"))
            )
            .where(m.year.eq(year))
            .orderBy(ci.clubNm.asc())
    );
  }

  /** ✅ 계산 모드용: 클럽별 회원수 집계 + 동호회장명 */
  public <T> SelectQuery<T> selectClubMemberAgg(Class<T> type) {
    QClubInfo ci = QClubInfo.clubInfo;
    QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    QCoEmplBas ceb = QCoEmplBas.coEmplBas;

    var leaderExpr = Expressions.stringTemplate(
        "coalesce({0} || ' ' || {1}, '미상')",
        ceb.nameKo, ceb.positionCd
    );

    var memberCntExpr = cui.clubUserId.count().intValue();

    return new SelectQuery<>(
        queryFactory
            .select(Projections.constructor(
                type,
                ci.clubId,
                ci.clubNm,
                leaderExpr,
                memberCntExpr
            ))
            .from(ci)
            .leftJoin(cui).on(
                ci.clubId.eq(cui.clubId)
                    .and(cui.status.eq("20"))
            )
            .leftJoin(ceb).on(
                ceb.empNo.eq(ci.clubMasterId)
                    .and(ceb.deleteAt.ne("Y"))
            )
            .where(ci.status.notIn("40", "50"))
            .groupBy(ci.clubId, ci.clubNm, ceb.nameKo, ceb.positionCd)
            .orderBy(ci.clubNm.asc())
    );
  }

  // ============================================================
  // ✅✅ 여기부터 "추가" (니 코드에 덧붙임)
  // ============================================================

  /** ✅ 업서트용: (clubId, year) 단건 조회 */
  public ClubApplyFeeManage findManageByClubIdYear(Long clubId, String year) {
    QClubApplyFeeManage m = QClubApplyFeeManage.clubApplyFeeManage;

    return queryFactory
        .selectFrom(m)
        .where(
            m.clubId.eq(clubId),
            m.year.eq(year)
        )
        .fetchFirst();
  }

  /**
   * ✅ 계산 모드(Manage 없을 때) : 클럽별 회원수 + "오늘 기준" 적용중 규정(pay_amount)까지 같이 뽑기
   *
   * 리턴 컬럼 순서(생성자 DTO 맞춰라):
   * clubId, clubNm, clubLeader, memberCnt, supportAmount, payYn
   */
  public <T> SelectQuery<T> selectCalcRowsWithSupportAmount(Class<T> type, String year) {

	  QClubInfo ci = QClubInfo.clubInfo;
	  QClubUserInfo cui = QClubUserInfo.clubUserInfo;
	  QCoEmplBas ceb = QCoEmplBas.coEmplBas;

	  QClubApplyFeeRuleBas bas = QClubApplyFeeRuleBas.clubApplyFeeRuleBas;
	  QClubApplyFeeRuleDetail det = QClubApplyFeeRuleDetail.clubApplyFeeRuleDetail;

	  var leaderExpr = Expressions.stringTemplate(
	      "coalesce({0} || ' ' || {1}, '미상')",
	      ceb.nameKo, ceb.positionCd
	  );

	  var memberCntLong = cui.clubUserId.count();
	  var memberCntInt = memberCntLong.intValue();

	  // ✅ year 기준일(예: 2025-12-31) - 그 해의 마지막 날로 잡는게 보통 안전함
	  LocalDate base = LocalDate.of(Integer.parseInt(year.trim()), 12, 31);
	  var baseDate = Expressions.dateTemplate(LocalDate.class, "{0}", base);

	  // ✅ 그 기준일에 “적용중”인 applyId
	  var currentApplyId =
	      JPAExpressions
	          .select(bas.applyId)
	          .from(bas)
	          .where(
	              bas.useYn.eq("Y"),
	              bas.applyStartDt.loe(baseDate),
	              bas.applyEndDt.isNull().or(bas.applyEndDt.goe(baseDate))
	          )
	          .orderBy(bas.applyId.desc())
	          .limit(1);

	  // ✅ 회원수 구간 매칭 payAmount
	  var payAmountSub =
	      JPAExpressions
	          .select(det.payAmount)
	          .from(det)
	          .where(
	              det.applyId.eq(currentApplyId),
	              memberCntLong.goe(det.memberCntFrom.longValue()),
	              det.memberCntTo.isNull().or(memberCntLong.loe(det.memberCntTo.longValue()))
	          )
	          .limit(1);

	  var supportAmountExpr =
	      Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", payAmountSub);

	  return new SelectQuery<>(
	      queryFactory
	          .select(Projections.constructor(
	              type,
	              ci.clubId,
	              ci.clubNm,
	              leaderExpr,
	              memberCntInt,
	              supportAmountExpr,
	              Expressions.constant("N")
	          ))
	          .from(ci)
	          .leftJoin(cui).on(
	              ci.clubId.eq(cui.clubId).and(cui.status.eq("20"))
	          )
	          .leftJoin(ceb).on(
	              ceb.empNo.eq(ci.clubMasterId).and(ceb.deleteAt.ne("Y"))
	          )
	          .where(ci.status.notIn("40", "50"))
	          .groupBy(ci.clubId, ci.clubNm, ceb.nameKo, ceb.positionCd)
	          .orderBy(ci.clubNm.asc())
	  );
	}
}
