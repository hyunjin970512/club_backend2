package kr.co.koreazinc.temp.repository.form;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.detail.ClubFeeInfo;
import kr.co.koreazinc.temp.model.entity.detail.QClubFeeInfo;

@Repository
@Transactional(readOnly = true)
public class ClubFeeInfoRepository extends AbstractJpaRepository<ClubFeeInfo, Integer> {

    public ClubFeeInfoRepository(List<EntityManager> entityManagers) {
        super(ClubFeeInfo.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {
        private final QClubFeeInfo fee = QClubFeeInfo.clubFeeInfo;
        public SelectQuery(JPAQuery<DTO> query) { super(query); }
    }

    public boolean existsByClubIdAndPositionCd(Integer clubId, String positionCd) {
        QClubFeeInfo fee = QClubFeeInfo.clubFeeInfo;

        Integer one = queryFactory
            .selectOne()
            .from(fee)
            .where(
                fee.clubId.eq(clubId),
                fee.positionCd.eq(positionCd)
            )
            .fetchFirst();

        return one != null;
    }

    @Transactional
    public void insertDefaultFee(Integer clubId, String positionCd, Integer amount, String createUser) {
        // ✅ 중복 방지 (uk_club_fee)
        if (existsByClubIdAndPositionCd(clubId, positionCd)) return;

        ClubFeeInfo fee = ClubFeeInfo.builder()
            .clubId(clubId)
            .positionCd(positionCd)
            .amount(amount)
            .createUser(createUser)
            // DB default(now()) 쓰면 생략 가능. 그래도 확실히 박고 싶으면 아래 유지
            .createDate(LocalDateTime.now())
            .build();

        save(fee);
    }

    @Transactional
    public void insertDefaultFees(Integer clubId, String createUser) {
        insertDefaultFee(clubId, "20", 60000, createUser);
        insertDefaultFee(clubId, "10", 40000, createUser);
        insertDefaultFee(clubId, "00", 20000, createUser);
    }
}
