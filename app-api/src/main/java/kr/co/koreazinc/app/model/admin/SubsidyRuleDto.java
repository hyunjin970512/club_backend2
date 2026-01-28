package kr.co.koreazinc.app.model.admin;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

public class SubsidyRuleDto {

    // -------------------
    // 저장 요청 DTO (프론트 payload)
    // -------------------
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private LocalDate applyStartDt;
        private LocalDate applyEndDt;
        private List<CreateDetail> details;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDetail {
        private Integer memberCntFrom;
        private Integer memberCntTo;   // null 가능
        private Integer payAmount;
    }

    // -------------------
    // 이력 조회 Row
    // -------------------
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryRow {
        private Long applyId;
        private LocalDate applyStartDt;
        private LocalDate applyEndDt;
        private String useYn;
    }

    // -------------------
    // 상세 조회 Row
    // -------------------
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor // (lineId, applyId, from, to, payAmount, rangeText) 6개 생성자
    public static class CurrentDetailRow {
        private Long lineId;
        private Long applyId;
        private Integer memberCntFrom;
        private Integer memberCntTo;
        private Integer payAmount;
        private String rangeText;

        // ✅ QueryDSL Projections.constructor가 찾을 5개짜리 생성자 (rangeText 제외)
        public CurrentDetailRow(
                Long lineId,
                Long applyId,
                Integer memberCntFrom,
                Integer memberCntTo,
                Integer payAmount
        ) {
            this.lineId = lineId;
            this.applyId = applyId;
            this.memberCntFrom = memberCntFrom;
            this.memberCntTo = memberCntTo;
            this.payAmount = payAmount;
        }
    }
}
