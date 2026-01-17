package kr.co.koreazinc.app.service.account;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.koreazinc.app.model.account.AccountDto;
import kr.co.koreazinc.data.types.Condition;
import kr.co.koreazinc.spring.http.util.PageResponse;
import kr.co.koreazinc.spring.util.validation.ValidList;
import kr.co.koreazinc.temp.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountDto.Get get(UUID key) {
        return accountRepository.selectQuery(AccountDto.Get.class).eqKey(key).fetchOne();
    }

    public PageResponse<AccountDto.Get> get(Condition condition) {
        List<AccountDto.Get> result = accountRepository.selectQuery(AccountDto.Get.class)
                .condition(condition).stream().collect(Collectors.toCollection(ValidList::new));

        Long totalCount = accountRepository.selectCountQuery().condition(condition).fetchOne();

        return PageResponse.of(result, totalCount);
    }

    @Transactional
    public AccountDto.Get post(AccountDto.Post dto) {
        return this.get(accountRepository.insert(dto).getAccountId());
    }

    @Transactional
    public List<AccountDto.Get> post(List<AccountDto.Post> dtos) {
        return dtos.stream().map(dto -> this.post(dto)).collect(Collectors.toList());
    }

    @Transactional
    public AccountDto.Get put(AccountDto.Put dto) {
        return this.get(accountRepository.save(dto).getAccountId());
    }

    @Transactional
    public List<AccountDto.Get> put(List<AccountDto.Put> dtos) {
        return dtos.stream().map(dto -> this.put(dto)).collect(Collectors.toList());
    }

    @Transactional
    public AccountDto.Get patch(AccountDto.Patch dto) {
        return this.put(dto.of(accountRepository.findOne(dto.getKey())));
    }

    @Transactional
    public List<AccountDto.Get> patch(List<AccountDto.Patch> dtos) {
        return dtos.stream().map(dto -> this.patch(dto)).collect(Collectors.toList());
    }

    @Transactional
    public HttpStatus delete(AccountDto.Delete dto) {
        accountRepository.delete(dto.getKey());
        return HttpStatus.NO_CONTENT;
    }

    @Transactional
    public HttpStatus delete(List<AccountDto.Delete> dtos) {
        dtos.forEach(dto -> this.delete(dto));
        return HttpStatus.NO_CONTENT;
    }
}
