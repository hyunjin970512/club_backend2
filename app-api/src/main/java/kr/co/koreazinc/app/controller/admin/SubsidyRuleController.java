package kr.co.koreazinc.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.service.admin.SubsidyRuleService;
import kr.co.koreazinc.app.model.admin.SubsidyRuleDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/subsidy-rules")
public class SubsidyRuleController {

    private final SubsidyRuleService subsidyRuleService;

    /** 지급규정 이력(기간 목록) */
    @GetMapping("/history")
    public List<SubsidyRuleDto.HistoryRow> history() {
        return subsidyRuleService.getHistory();
    }

    /** 선택 applyId의 상세 규정(테이블 rows) */
    @GetMapping("/{applyId}/details")
    public List<SubsidyRuleDto.CurrentDetailRow> details(@PathVariable("applyId") Long applyId) {
        return subsidyRuleService.getRuleDetails(applyId);
    }

    /** (옵션) 현재 적용중 규정 상세 */
    @GetMapping("/current/details")
    public List<SubsidyRuleDto.CurrentDetailRow> currentDetails() {
        return subsidyRuleService.getCurrentRuleDetails();
    }
}
