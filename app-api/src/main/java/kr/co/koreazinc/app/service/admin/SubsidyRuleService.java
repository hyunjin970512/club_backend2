package kr.co.koreazinc.app.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.admin.SubsidyRuleDto;
import kr.co.koreazinc.temp.model.entity.admin.SubsidyRuleCommandRow;
import kr.co.koreazinc.temp.repository.admin.ApplyFeeRuleRepository;
import kr.co.koreazinc.temp.repository.admin.SubsidyRuleCommandRepository;

@Service
@RequiredArgsConstructor
public class SubsidyRuleService {

    // ✅ 조회용(니가 원래 쓰던 레포)
    private final ApplyFeeRuleRepository queryRepo;

    // ✅ 저장/비활성화용(내가 만든 레포)
    private final SubsidyRuleCommandRepository commandRepo;

    // -------------------
    // 조회
    // -------------------
    @Transactional(readOnly = true)
    public List<SubsidyRuleDto.HistoryRow> getHistory() {
        return queryRepo.selectHistory(SubsidyRuleDto.HistoryRow.class)
                        .fetch();
    }

    @Transactional(readOnly = true)
    public List<SubsidyRuleDto.CurrentDetailRow> getCurrentRuleDetails() {

        List<SubsidyRuleDto.CurrentDetailRow> rows =
                queryRepo.selectCurrentRuleDetails(SubsidyRuleDto.CurrentDetailRow.class)
                         .orderByLineId()
                         .fetch();

        rows.forEach(r -> r.setRangeText(makeRangeText(r.getMemberCntFrom(), r.getMemberCntTo())));
        return rows;
    }

    @Transactional(readOnly = true)
    public List<SubsidyRuleDto.CurrentDetailRow> getRuleDetails(Long applyId) {

        List<SubsidyRuleDto.CurrentDetailRow> rows =
                queryRepo.selectRuleDetailsByApplyId(SubsidyRuleDto.CurrentDetailRow.class, applyId)
                         .orderByLineId()
                         .fetch();

        rows.forEach(r -> r.setRangeText(makeRangeText(r.getMemberCntFrom(), r.getMemberCntTo())));
        return rows;
    }

    // -------------------
    // 저장 (CREATE)
    // -------------------
    @Transactional
    public Long createRule(SubsidyRuleDto.CreateRequest req, String empNo) {

        // 1) 현재 적용중 규정 있으면 use_yn='N'
        commandRepo.deactivateCurrentRules(empNo);

        // 2) BAS 생성
        Long applyId = commandRepo.insertBas(req.getApplyStartDt(), req.getApplyEndDt(), empNo);

        // 3) DETAIL 생성 (프론트 payload -> command row 변환)
        List<SubsidyRuleCommandRow> rows = (req.getDetails() == null ? List.of() :
                req.getDetails().stream()
                   .map(d -> new SubsidyRuleCommandRow(
                           d.getMemberCntFrom(),
                           d.getMemberCntTo(),
                           d.getPayAmount()
                   ))
                   .toList()
        );

        commandRepo.insertDetails(applyId, rows, empNo);

        return applyId;
    }

    private String makeRangeText(Integer from, Integer to) {
        if (from == null) return "-";
        if (to == null) return from + "명 이상";
        return from + "명 이상 " + to + "명 이하";
    }
}
