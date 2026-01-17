package kr.co.koreazinc.temp.model.converter.account;

import kr.co.koreazinc.data.model.converter.EntityConverter;
import kr.co.koreazinc.temp.model.entity.account.Account;

public class AccountConverter extends EntityConverter<Account.Getter, Account> {

    public AccountConverter(Account.Getter origin) {
        super(origin);
    }

    @Override
    public Account toEntity() {
        return Account.builder().accountId(this.origin.getAccountId())
                .userId(this.origin.getUserId()).userPw(this.origin.getUserPw())
                .displayNm(this.origin.getDisplayNm()).accountName(this.origin.getAccountName())
                .accountDesc(this.origin.getAccountDesc()).use(this.origin.getUse()).build();
    }
}
