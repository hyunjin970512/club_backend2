package kr.co.koreazinc.app.model.admin;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubsidyRuleCreateRequest {

    private LocalDate applyStartDt;
    private LocalDate applyEndDt;
    private List<Detail> details;

    @Getter @Setter
    public static class Detail {
        private Integer memberCntFrom;
        private Integer memberCntTo;
        private Integer payAmount;
    }
}
