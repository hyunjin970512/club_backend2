package kr.co.koreazinc.app.controller.v1.account;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.koreazinc.app.model.account.AccountDto;
import kr.co.koreazinc.app.service.account.AccountService;
import kr.co.koreazinc.data.types.Condition;
import kr.co.koreazinc.spring.http.util.PageResponse;
import kr.co.koreazinc.spring.util.validation.ValidList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/account")
@Tag(name = "AccountController", description = "계정 REST API")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "계정 조회")
    public ResponseEntity<PageResponse<AccountDto.Get>> get(@ModelAttribute Condition condition) {
        return ResponseEntity.ok(accountService.get(condition));
    }

    @PostMapping
    @Operation(summary = "계정 등록")
    public ResponseEntity<List<AccountDto.Get>> post(
            @Valid @RequestBody ValidList<AccountDto.Post> dtos) {
        return ResponseEntity.ok(accountService.post(dtos));
    }

    @PutMapping
    @Operation(summary = "계정 수정")
    public ResponseEntity<List<AccountDto.Get>> put(
            @Valid @RequestBody ValidList<AccountDto.Put> dtos) {
        return ResponseEntity.ok(accountService.put(dtos));
    }

    @PatchMapping
    @Operation(summary = "계정 부분 수정")
    public ResponseEntity<List<AccountDto.Get>> patch(
            @Valid @RequestBody ValidList<AccountDto.Patch> dtos) {
        return ResponseEntity.ok(accountService.patch(dtos));
    }

    @DeleteMapping
    @Operation(summary = "계정 삭제")
    public ResponseEntity<Void> delete(@Valid @RequestBody ValidList<AccountDto.Delete> dtos) {
        return new ResponseEntity<>(accountService.delete(dtos));
    }
}
