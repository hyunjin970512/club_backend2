package kr.co.koreazinc.app.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.admin.SubsidyRuleDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.admin.SubsidyRuleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/subsidy-rules")
public class SubsidyRuleAdminController {

    private final SubsidyRuleService subsidyRuleService;
    private final CurrentUserService currentUser;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody SubsidyRuleDto.CreateRequest req
    ) {
        subsidyRuleService.createRule(req, currentUser.empNoOrThrow());
        return ResponseEntity.ok().build();
    }
}
