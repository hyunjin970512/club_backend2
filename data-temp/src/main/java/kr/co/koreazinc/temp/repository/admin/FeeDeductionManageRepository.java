package kr.co.koreazinc.temp.repository.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.account.QCoEmplBas;
import kr.co.koreazinc.temp.model.entity.detail.QClubFeeInfo;
import kr.co.koreazinc.temp.model.entity.main.ClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QCoCommonCode;

@Repository
@Transactional(readOnly = true)
public class FeeDeductionManageRepository
    extends AbstractJpaRepository<ClubInfo, Long> {

  public FeeDeductionManageRepository(List<EntityManager> entityManagers) {
    super(ClubInfo.class, entityManagers);
  }

  /** 체이닝용 */
  public class SelectQuery<DTO> extends Query.Select<DTO> {
    public SelectQuery(JPAQuery<DTO> query) { super(query); }
  }

  /**
   * ✅ yyyymm 기준으로 cui.create_date 필터
   * - yyyymm: "202601"
   * - 범위: 2026-01-01 00:00:00 <= create_date < 2026-02-01 00:00:00
   */
  public <T> SelectQuery<T> selectRows(Class<T> type, String yyyymm) {

    QCoEmplBas ceb = QCoEmplBas.coEmplBas;
    QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    QClubInfo ci = QClubInfo.clubInfo;
    QClubFeeInfo cfi = QClubFeeInfo.clubFeeInfo;
    QCoCommonCode ccc = QCoCommonCode.coCommonCode;

    // ✅ yyyymm -> start/end LocalDateTime
    LocalDate startDay = LocalDate.parse(yyyymm + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDateTime start = startDay.atStartOfDay();
    LocalDateTime end = startDay.plusMonths(1).atStartOfDay();

    DateTimeExpression<LocalDateTime> startExpr =
        Expressions.dateTimeTemplate(LocalDateTime.class, "{0}", start);
    DateTimeExpression<LocalDateTime> endExpr =
        Expressions.dateTimeTemplate(LocalDateTime.class, "{0}", end);

    /**
     * ✅ POSITION_CD 매핑 (여기 핵심 수정!)
     * - cfi.position_cd 가 String 이니까
     * - sub_code도 String으로 뽑아야 함
     */
    var positionSubCode =
        JPAExpressions
            .select(ccc.id.subCode)   // ✅ String
            .from(ccc)
            .where(
                ccc.id.mainCode.eq("POSITION_CD"),
                ccc.codeNm.eq(ceb.positionCd),
                ccc.useAt.eq("Y"),
                ccc.deleteAt.eq("N")
            );

    // ✅ empNo 기준 total sum window
    var totalAmountExpr =
        Expressions.numberTemplate(
            Integer.class,
            "sum({0}) over (partition by {1})",
            cfi.amount,
            ceb.empNo
        );

    var amountExpr =
        Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", cfi.amount);

    var totalCoalesceExpr =
        Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", totalAmountExpr);

    // ✅ clubId 타입 안 맞는 경우 대비 (cfi.clubId가 Integer일 때)
    var ciClubIdAsInteger =
        Expressions.numberTemplate(Integer.class, "{0}", ci.clubId);

    return new SelectQuery<>(
        queryFactory
            .select(Projections.constructor(
                type,
                ceb.empNo,
                ceb.deptCd,
                ceb.nameKo,
                ceb.positionCd,
                ci.clubNm,
                amountExpr,
                totalCoalesceExpr
            ))
            .from(ceb)
            .join(cui).on(ceb.empNo.eq(cui.empNo))
            .join(ci).on(ci.clubId.eq(cui.clubId))
            .join(cfi).on(
                cfi.clubId.eq(ciClubIdAsInteger)
                    .and(cfi.positionCd.eq(positionSubCode)) // ✅ String = String 서브쿼리
            )
            .where(
                cui.status.eq("10"),
                ci.status.in("20", "30"),
                // ✅ yyyymm 기준 가입일(create_date) 필터
                //cui.createDate.goe(startExpr),
                cui.createDate.lt(endExpr)
            )
            .orderBy(ceb.empNo.asc(), ci.clubNm.asc())
    );
  }
}
