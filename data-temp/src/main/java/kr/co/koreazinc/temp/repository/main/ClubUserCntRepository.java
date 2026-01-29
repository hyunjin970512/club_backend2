package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class ClubUserCntRepository extends AbstractJpaRepository<ClubUserInfo, Long> {

    private static final String ACTIVE_STATUS = "10";

    public ClubUserCntRepository(List<EntityManager> entityManagers) {
        super(ClubUserInfo.class, entityManagers);
    }

    /** 체이닝용 */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        private final QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> eqEmpNo(String empNo) {
            query.where(cui.empNo.eq(empNo));
            return this;
        }

        public SelectQuery<DTO> activeOnly() {
            query.where(cui.status.eq(ACTIVE_STATUS));
            return this;
        }
    }

    /** ✅ 가입 동호회 개수 조회 (status = 'ACTIVE') */
    public int countActiveClubsByEmpNo(String empNo) {
        QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        Long cnt = queryFactory
            .select(cui.count())
            .from(cui)
            .where(
                cui.empNo.eq(empNo),
                cui.status.eq(ACTIVE_STATUS)
            )
            .fetchOne();

        return cnt == null ? 0 : cnt.intValue();
    }
}
