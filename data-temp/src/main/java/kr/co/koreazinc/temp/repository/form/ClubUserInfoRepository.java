package kr.co.koreazinc.temp.repository.form;

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
public class ClubUserInfoRepository extends AbstractJpaRepository<ClubUserInfo, Long> {

    public ClubUserInfoRepository(List<EntityManager> entityManagers) {
        super(ClubUserInfo.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {
        private final QClubUserInfo cui = QClubUserInfo.clubUserInfo;
        public SelectQuery(JPAQuery<DTO> query) { super(query); }
    }

    /** uk_club_user (club_id, emp_no) 중복 체크 */
    public boolean existsByClubIdAndEmpNo(Long clubId, String empNo) {
        QClubUserInfo cui = QClubUserInfo.clubUserInfo;

        Integer one = queryFactory
            .selectOne()
            .from(cui)
            .where(
                cui.clubId.eq(clubId),
                cui.empNo.eq(empNo)
            )
            .fetchFirst();

        return one != null;
    }

    /**
     * ✅ 동호회 신설 시 생성자 자동 가입/등록
     * - user_role_cd: '00' (동호회 관리자 권한)
     * - status: '20' (가입/승인)
     * - join_date/create_date/update_date: 엔티티 Builder에서 now로 세팅됨
     * - create_user/update_user: empNo
     */
    @Transactional
    public void insertCreatorAsMember(Long clubId, String empNo) {
        // 중복 방지
        if (existsByClubIdAndEmpNo(clubId, empNo)) return;

        ClubUserInfo row = ClubUserInfo.builder()
            .clubId(clubId)
            .empNo(empNo)
            .userRoleCd("00")
            .status("20")
            .createUser(empNo)
            .build();

        save(row);
    }
}
