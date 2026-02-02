package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.account.QCoEmplBas;
import kr.co.koreazinc.temp.model.entity.main.QCoCommonCode;
import kr.co.koreazinc.temp.model.entity.together.QTogetherBoard;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;

@Repository
@Transactional(readOnly = true)
public class GetTogetherBoardRepository extends AbstractJpaRepository<TogetherBoard, Long> {

    public GetTogetherBoardRepository(List<EntityManager> entityManagers) {
        super(TogetherBoard.class, entityManagers);
    }

    /** 체이닝용 */
    public class SelectQuery<DTO> extends Query.Select<DTO> {
        private final QTogetherBoard tb = QTogetherBoard.togetherBoard;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> orderByLatest() {
            query.orderBy(tb.createDate.desc(), tb.boardId.desc());
            return this;
        }
    }

    /**
     * 투게더 목록 조회
     * - siteType: ALL | HQ | OS | ET
     * - type    : ALL | 10 | 20
     * - q       : 전체검색 (제목/내용/작성자명)
     */
    public <T> SelectQuery<T> selectBoardList(Class<T> dtoType, String siteType, String type, String q) {

        QTogetherBoard tb = QTogetherBoard.togetherBoard;
        QCoEmplBas ceb = QCoEmplBas.coEmplBas;

        // co_common_code alias 2개 (같은 테이블 2번 조인)
        QCoCommonCode clubType = new QCoCommonCode("clubType");         // CLUB_TYPE
        QCoCommonCode togetherType = new QCoCommonCode("togetherType"); // TOGETHER_TYPE

        // ✅ 기본 where (삭제 제외)
        BooleanBuilder where = new BooleanBuilder();
        where.and(tb.deleteYn.ne("Y"));

        // ✅ 사업장 필터
        if (StringUtils.hasText(siteType) && !"ALL".equalsIgnoreCase(siteType)) {
            where.and(tb.clubCode.eq(siteType));
        }

        // ✅ 팀/반 필터
        if (StringUtils.hasText(type) && !"ALL".equalsIgnoreCase(type)) {
            where.and(tb.togetherCode.eq(type));
        }

        // ✅ 검색어: 제목 OR 내용 OR 작성자명
        if (StringUtils.hasText(q)) {
            String kw = q.trim();

            BooleanBuilder search = new BooleanBuilder();
            search.or(tb.title.containsIgnoreCase(kw));
            search.or(tb.content.containsIgnoreCase(kw));
            search.or(ceb.nameKo.containsIgnoreCase(kw));

            where.and(search);
        }

        JPAQuery<T> base = queryFactory
            .select(Projections.constructor(
                dtoType,
                tb.boardId,
                tb.createUser,
                ceb.nameKo,

                clubType.id.subCode,   // siteType (club_code)
                clubType.codeNm,       // siteName

                tb.title,

                togetherType.id.subCode, // type (together_code)
                togetherType.codeNm,     // typeName

                tb.viewCnt,
                tb.recomendCnt
            ))
            .from(tb)
            .join(ceb).on(
                tb.createUser.eq(ceb.empNo),
                ceb.deleteAt.ne("Y")
            )
            .join(clubType).on(
                tb.clubCode.eq(clubType.id.subCode),
                clubType.id.mainCode.eq("CLUB_TYPE"),
                clubType.id.subCode.ne("@"),
                clubType.useAt.eq("Y"),
                clubType.deleteAt.eq("N")
            )
            .join(togetherType).on(
                tb.togetherCode.eq(togetherType.id.subCode),
                togetherType.id.mainCode.eq("TOGETHER_TYPE"),
                togetherType.id.subCode.ne("@"),
                togetherType.useAt.eq("Y"),
                togetherType.deleteAt.eq("N")
            )
            .where(where);

        return new SelectQuery<>(base);
    }
}
