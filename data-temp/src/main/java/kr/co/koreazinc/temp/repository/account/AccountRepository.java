package kr.co.koreazinc.temp.repository.account;

import static kr.co.koreazinc.temp.model.entity.account.QAccount.account;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.data.support.Query.Procedure;
import kr.co.koreazinc.temp.model.converter.account.AccountConverter;
import kr.co.koreazinc.temp.model.entity.account.Account;

@Repository
@Transactional(readOnly = true)
public class AccountRepository extends AbstractJpaRepository<Account, UUID> {

    public AccountRepository(@Autowired List<EntityManager> entityManagers) {
        super(Account.class, entityManagers);
    }

    public class SelectQuery<DTO> extends Query.Select<DTO> {

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
            this.addField("accountId", account.accountId);
            this.addField("userId", account.userId);
            this.addField("userPw", account.userPw);
            this.addField("displayNm", account.displayNm);
            this.addField("accountDesc", account.accountDesc);
        }

        public SelectQuery<DTO> eqKey(UUID key) {
            query.where(account.accountId.eq(key));
            return this;
        }

        public SelectQuery<DTO> inKey(Collection<UUID> keys) {
            query.where(account.accountId.in(keys));
            return this;
        }

        public SelectQuery<DTO> eqUserId(String userId) {
            query.where(account.userId.eq(userId));
            return this;
        }
    }

    public SelectQuery<Account> selectQuery() {
        return new SelectQuery<>(queryFactory.selectFrom(account));
    }

    /** 프로시저 사용법 1 */
    public class SelectTestQuery<DTO> extends Query<DTO> {

        public SelectTestQuery(jakarta.persistence.Query query) {
            super(query);
        }

        public SelectTestQuery<DTO> setMainCode(String mainCode) {
            query.setParameter("mainCode", mainCode);
            return this;
        }

        public SelectTestQuery<DTO> setSubCode(String subCode) {
            query.setParameter("subCode", subCode);
            return this;
        }

        public SelectTestQuery<DTO> setCodeLength(Integer codeLength) {
            query.setParameter("codeLength", codeLength);
            return this;
        }
    }

    public <T extends Account.Setter> SelectTestQuery<T> selectTestQuery(Class<T> type) {
        return new SelectTestQuery<T>(entityManager.createNativeQuery(
                "EXEC sp_CommonCode_Test :mainCode, :subCode, :codeLength, ''", type)).bean(type,
                        (alias) -> {
                            return StringUtils.uncapitalize(alias);
                        });
    }

    /** end */

    /** 프로시저 사용법 2 */
    public class CommonCodeTest<DTO> extends Procedure<DTO> {

        public CommonCodeTest(Class<DTO> type) {
            super(entityManager.createStoredProcedureQuery("sp_CommonCode_Test", type));
            storedProcedure.registerStoredProcedureParameter("mainCode", String.class,
                    ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("subCode", String.class,
                    ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("codeLength", Integer.class,
                    ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("returnCode", String.class,
                    ParameterMode.OUT);
        }

        public CommonCodeTest<DTO> setMainCode(String mainCode) {
            storedProcedure.setParameter("mainCode", mainCode);
            return this;
        }

        public CommonCodeTest<DTO> setSubCode(String subCode) {
            storedProcedure.setParameter("subCode", subCode);
            return this;
        }

        public CommonCodeTest<DTO> setCodeLength(Integer codeLength) {
            storedProcedure.setParameter("codeLength", codeLength);
            return this;
        }

        public String getReturnCode() {
            return this.getValue("returnCode");
        }
    }

    public <T extends Account.Setter> CommonCodeTest<T> execCommonCodeTest(Class<T> type) {
        return new CommonCodeTest<T>(type);
    }

    /** end */

    public <T extends Account.Setter> SelectQuery<T> selectQuery(Class<T> type) {
        return new SelectQuery<>(queryFactory
                .select(Projections.bean(type, account.accountId, account.userId, account.userPw,
                        // , CustomDialect.getCodeName(commonCode.key.mainCode,
                        // commonCode.key.subCode).as("codeName"), DB 함수 사용법
                        account.displayNm, account.accountName, account.accountDesc, account.use))
                .from(account));
    }

    public SelectQuery<Long> selectCountQuery() {
        return new SelectQuery<>(queryFactory.select(account.count()).from(account));
    }

    @Transactional
    public Account insert(Account.Getter getter) {
        return this.insert(new AccountConverter(getter).toEntity());
    }

    @Transactional
    public Collection<Account> insert(Collection<? extends Account.Getter> getters) {
        return getters.stream().map(getter -> this.insert(new AccountConverter(getter).toEntity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Account save(Account.Getter getter) {
        return this.save(new AccountConverter(getter).toEntity());
    }

    @Transactional
    public Collection<Account> save(Collection<? extends Account.Getter> getters) {
        return getters.stream().map(getter -> this.save(new AccountConverter(getter).toEntity()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Account.Getter getter) {
        this.delete(new AccountConverter(getter).toEntity());
    }

    @Transactional
    public void delete(Collection<? extends Account.Getter> getters) {
        getters.stream().forEach(getter -> this.delete(new AccountConverter(getter).toEntity()));
    }
}
