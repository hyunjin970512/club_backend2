package kr.co.koreazinc.app.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FeeDeductionDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Row {
    private String empNo;
    private String deptCd;
    private String nameKo;
    private String positionCd;
    private String clubNm;
    private Integer amount;       // 개별 공제액
    private Integer totalAmount;  // empNo 총합(윈도우)
  }
}
