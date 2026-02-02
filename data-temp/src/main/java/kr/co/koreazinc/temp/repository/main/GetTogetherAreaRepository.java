package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.CoCommonCode;
import kr.co.koreazinc.temp.model.entity.main.CoCommonCodeId;
import kr.co.koreazinc.temp.model.entity.main.QCoCommonCode;

@Repository
@Transactional(readOnly = true)
public class GetTogetherAreaRepository extends AbstractJpaRepository<CoCommonCode, CoCommonCodeId> {

    private static final String MAIN_CODE_CLUB_TYPE = "CLUB_TYPE";

    public GetTogetherAreaRepository(List<EntityManager> entityManagers) {
        super(CoCommonCode.class, entityManagers);
    }

    /** ✅ 체이닝용 래퍼 (MenuRepository 스타일) */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        private final QCoCommonCode ccc = QCoCommonCode.coCommonCode;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> eqMainCode(String mainCode) {
            query.where(ccc.id.mainCode.eq(mainCode));
            return this;
        }

        public SelectQuery<DTO> neSubCode(String subCode) {
            query.where(ccc.id.subCode.ne(subCode));
            return this;
        }

        public SelectQuery<DTO> onlyUseY() {
            query.where(ccc.useAt.eq("Y"), ccc.deleteAt.eq("N"));
            return this;
        }

        public SelectQuery<DTO> orderByUpdateDateAsc() {
            query.orderBy(ccc.updateDate.asc());
            return this;
        }
    }

    /**
     * ✅ 사업장(칩) 목록 조회 (DTO 주입 버전)
     * - main_code = 'TOGETHER_TYPE'
     * - sub_code != '@'
     * - ORDER BY update_date ASC
     */
    public <T> SelectQuery<T> selectAreas(Class<T> type) {

        QCoCommonCode ccc = QCoCommonCode.coCommonCode;

        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    ccc.id.subCode, // areaCd
                    ccc.codeNm      // areaNm
                ))
                .from(ccc)
        )
        .eqMainCode(MAIN_CODE_CLUB_TYPE)
        .neSubCode("@")
        .onlyUseY()
        .orderByUpdateDateAsc();
    }

    /** 엔티티 직접 조회용 */
    public SelectQuery<CoCommonCode> selectQuery() {
        QCoCommonCode ccc = QCoCommonCode.coCommonCode;
        return new SelectQuery<>(queryFactory.selectFrom(ccc));
    }
}
