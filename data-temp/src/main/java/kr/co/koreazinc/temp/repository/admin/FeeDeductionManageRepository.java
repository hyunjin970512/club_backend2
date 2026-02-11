package kr.co.koreazinc.temp.repository.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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
import kr.co.koreazinc.temp.model.entity.main.QClubGwInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QCoCommonCode;

@Repository
@Transactional(readOnly = true)
public class FeeDeductionManageRepository extends AbstractJpaRepository<ClubInfo, Long> {

  public FeeDeductionManageRepository(List<EntityManager> entityManagers) {
    super(ClubInfo.class, entityManagers);
  }

  /** 체이닝용 */
  public class SelectQuery<DTO> extends Query.Select<DTO> {
    public SelectQuery(JPAQuery<DTO> query) { super(query); }
  }

  /**
   * ✅ yyyymm 기준:
   * - club: ci.create_date < (조회월+1달)  => 누적 노출
   * - gw  : club_gw_info status='A' 인 동호회만
   * - fee : club_fee_info 중 "조건에 맞는 1건"만 선택
   *
   * (A) 룰:
   * - 클럽 생성월 == 조회월이면: fee 최초값 (min create_date)
   * - 그 외면: fee 최신값 (max create_date)
   * - 동일 create_date 여러 건이면: min(fee_id)로 1건 고정
   *
   * (중요) ORDER BY/LIMIT 없이 MIN/MAX 집계로 스칼라 1행 보장
   */
  public <T> SelectQuery<T> selectRows(Class<T> type, String yyyymm) {

    QCoEmplBas ceb = QCoEmplBas.coEmplBas;
    QClubUserInfo cui = QClubUserInfo.clubUserInfo;
    QClubInfo ci = QClubInfo.clubInfo;
    QClubGwInfo cgi = QClubGwInfo.clubGwInfo;

    QClubFeeInfo cfi = QClubFeeInfo.clubFeeInfo;
    QCoCommonCode ccc = QCoCommonCode.coCommonCode;

    // ✅ yyyymm -> q_month / q_next
    LocalDate startDay = LocalDate.parse(yyyymm + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDateTime qMonth = startDay.atStartOfDay();
    LocalDateTime qNext  = startDay.plusMonths(1).atStartOfDay();

    DateTimeExpression<LocalDateTime> qMonthExpr =
        Expressions.dateTimeTemplate(LocalDateTime.class, "{0}", qMonth);
    DateTimeExpression<LocalDateTime> qNextExpr =
        Expressions.dateTimeTemplate(LocalDateTime.class, "{0}", qNext);

    /**
     * ✅ POSITION_CD 매핑: ceb.positionCd -> sub_code
     * 스칼라 보장 위해 max()로 1행 강제 (중복 데이터 있어도 안 터짐)
     */
    var positionSubCode =
        JPAExpressions
            .select(ccc.id.subCode.max())
            .from(ccc)
            .where(
                ccc.id.mainCode.eq("POSITION_CD"),
                ccc.codeNm.eq(ceb.positionCd),
                ccc.useAt.eq("Y"),
                ccc.deleteAt.eq("N")
            );

    // ✅ club 생성월 == 조회월?
    var ciMonthExpr =
        Expressions.dateTimeTemplate(LocalDateTime.class, "date_trunc('month', {0})", ci.createDate);
    var isClubCreatedInQueryMonth = ciMonthExpr.eq(qMonthExpr);

    // ✅ 타입 맞추기: ci.clubId(Long) -> cfi.clubId(Integer)
    var ciClubIdAsInteger =
        Expressions.numberTemplate(Integer.class, "{0}", ci.clubId);

    // ====== ✅ fee_id를 "항상 1건"으로 만드는 서브쿼리 ======
    QClubFeeInfo cfi2 = new QClubFeeInfo("cfi2");
    QClubFeeInfo cfi3 = new QClubFeeInfo("cfi3");

    // (공통) 조회월(포함)까지 유효: create_date < qNext
    var minCreateDateSub =
        JPAExpressions
            .select(cfi2.createDate.min())
            .from(cfi2)
            .where(
                cfi2.clubId.eq(ciClubIdAsInteger),
                cfi2.positionCd.eq(positionSubCode),
                cfi2.createDate.lt(qNextExpr)
            );

    var maxCreateDateSub =
        JPAExpressions
            .select(cfi2.createDate.max())
            .from(cfi2)
            .where(
                cfi2.clubId.eq(ciClubIdAsInteger),
                cfi2.positionCd.eq(positionSubCode),
                cfi2.createDate.lt(qNextExpr)
            );

    // 동일 create_date 여러 건이면 fee_id 최소로 1건 고정
    var feeIdAtMinDateSub =
        JPAExpressions
            .select(cfi3.feeId.min())
            .from(cfi3)
            .where(
                cfi3.clubId.eq(ciClubIdAsInteger),
                cfi3.positionCd.eq(positionSubCode),
                cfi3.createDate.eq(minCreateDateSub)
            );

    var feeIdAtMaxDateSub =
        JPAExpressions
            .select(cfi3.feeId.min())
            .from(cfi3)
            .where(
                cfi3.clubId.eq(ciClubIdAsInteger),
                cfi3.positionCd.eq(positionSubCode),
                cfi3.createDate.eq(maxCreateDateSub)
            );

    // ✅ (A) 생성월==조회월 ? 최초 : 최신
    Expression<Integer> chosenFeeId =
        new CaseBuilder()
            .when(isClubCreatedInQueryMonth).then(feeIdAtMinDateSub)
            .otherwise(feeIdAtMaxDateSub);

    // ✅ empNo 기준 total sum window
    var totalAmountExpr =
        Expressions.numberTemplate(
            Integer.class,
            "sum({0}) over (partition by {1})",
            cfi.amount,
            ceb.empNo
        );

    var amountExpr = Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", cfi.amount);
    var totalCoalesceExpr = Expressions.numberTemplate(Integer.class, "coalesce({0}, 0)", totalAmountExpr);

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

            // ✅ GW 조건: status='A' 인 동호회만 (club_id 당 1건 보장이라 join OK)
            .join(cgi).on(
                cgi.clubId.eq(ci.clubId)
                    .and(cgi.status.eq("A"))
            )

            // ✅ fee는 "선택된 fee_id 1건"만 join
            .join(cfi).on(cfi.feeId.eq(chosenFeeId))

            .where(
                ceb.deleteAt.ne("Y"),
                cui.status.eq("10"),
                ci.status.eq("30"),

                // ✅ club 누적: 조회월+1달 이전 생성분
                ci.createDate.lt(qNextExpr)
            )
            .orderBy(ceb.empNo.asc(), ci.clubNm.asc())
    );
  }
}
