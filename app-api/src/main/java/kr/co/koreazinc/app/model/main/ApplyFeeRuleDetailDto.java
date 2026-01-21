package kr.co.koreazinc.app.model.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFeeRuleDetailDto {

    private Long lineId;
    private Long applyId;
    private Integer memberCntFrom;
    private Integer memberCntTo;
    private Integer payAmount;
}

