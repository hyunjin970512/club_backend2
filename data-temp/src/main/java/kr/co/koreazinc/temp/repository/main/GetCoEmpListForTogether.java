package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.account.CoEmplBas;
import kr.co.koreazinc.temp.model.entity.account.QCoEmplBas;

@Repository
@Transactional(readOnly = true)
public class GetCoEmpListForTogether extends AbstractJpaRepository<CoEmplBas, String> {

    public GetCoEmpListForTogether(List<EntityManager> entityManagers) {
        super(CoEmplBas.class, entityManagers);
    }

    /**
     * ✅ 투게더 푸시 대상자 사번 목록 조회 (emp_no만)
     *
     * SQL 등가:
     * SELECT emp_no
     * FROM co_empl_bas
     * WHERE delete_at != 'Y'
     *   AND use_at = 'Y'
     *   AND emp_no IS NOT NULL
     *   AND cmp_email IS NOT NULL;
     *
     * - 빈 문자열 이메일도 제외하려면 cmpEmail.ne("") 조건 추가 (기본 포함)
     */
    public List<String> selectEmpNoListForTogether() {
        QCoEmplBas ceb = QCoEmplBas.coEmplBas;

        return queryFactory
            .select(ceb.empNo)
            .from(ceb)
            .where(
                ceb.deleteAt.ne("Y"),
                ceb.useAt.eq("Y"),
                ceb.empNo.isNotNull(),
                ceb.cmpEmail.isNotNull(),
                ceb.cmpEmail.ne("")
            )
            .orderBy(ceb.empNo.asc())
            .fetch();
    }

    /**
     * (옵션) 검색 포함: 사번/이름/이메일에 키워드 포함된 대상자만
     * - emp_no만 리턴
     */
    public List<String> selectEmpNoListForTogether(String q) {
        QCoEmplBas ceb = QCoEmplBas.coEmplBas;

        JPAQuery<String> query = queryFactory
            .select(ceb.empNo)
            .from(ceb)
            .where(
                ceb.deleteAt.ne("Y"),
                ceb.useAt.eq("Y"),
                ceb.empNo.isNotNull(),
                ceb.cmpEmail.isNotNull(),
                ceb.cmpEmail.ne("")
            );

        if (StringUtils.hasText(q)) {
            String kw = q.trim();
            query.where(
                ceb.empNo.containsIgnoreCase(kw)
                    .or(ceb.nameKo.containsIgnoreCase(kw))
                    .or(ceb.cmpEmail.containsIgnoreCase(kw))
            );
        }

        return query
            .orderBy(ceb.nameKo.asc(), ceb.empNo.asc())
            .fetch();
    }

    /**
     * (옵션) 엔티티로 필요할 때만
     */
    public List<CoEmplBas> selectEmpEntityListForTogether(String q) {
        QCoEmplBas ceb = QCoEmplBas.coEmplBas;

        JPAQuery<CoEmplBas> query = queryFactory
            .selectFrom(ceb)
            .where(
                ceb.deleteAt.ne("Y"),
                ceb.useAt.eq("Y"),
                ceb.empNo.isNotNull(),
                ceb.cmpEmail.isNotNull(),
                ceb.cmpEmail.ne("")
            );

        if (StringUtils.hasText(q)) {
            String kw = q.trim();
            query.where(
                ceb.empNo.containsIgnoreCase(kw)
                    .or(ceb.nameKo.containsIgnoreCase(kw))
                    .or(ceb.cmpEmail.containsIgnoreCase(kw))
            );
        }

        return query
            .orderBy(ceb.nameKo.asc(), ceb.empNo.asc())
            .fetch();
    }
}
