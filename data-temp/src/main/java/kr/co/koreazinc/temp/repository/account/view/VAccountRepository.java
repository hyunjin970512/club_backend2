package kr.co.koreazinc.temp.repository.account.view;

import static kr.co.koreazinc.temp.model.entity.account.view.QVAccount.vAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.account.view.VAccount;

@Repository
@Transactional(readOnly = true)
public class VAccountRepository extends AbstractJpaRepository<VAccount, UUID> {

    public VAccountRepository(@Autowired List<EntityManager> entityManagers) {
        super(VAccount.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> eqKey(UUID key) {
            query.where(vAccount.id.eq(key));
            return this;
        }
    }

    public SelectQuery<VAccount> selectQuery() {
        return new SelectQuery<>(queryFactory.selectFrom(vAccount));
    }
}
