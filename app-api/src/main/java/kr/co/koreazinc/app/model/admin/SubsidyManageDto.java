package kr.co.koreazinc.app.model.admin;

import java.util.List;
import lombok.*;

public class SubsidyManageDto {

  @Data
  @NoArgsConstructor
  public static class Row {
    private Long manageId;
    private Long clubId;
    private String clubNm;
    private String clubLeader;
    private Integer memberCnt;
    private Integer supportAmount;
    private String payYn;
    private String source; // ✅ "MANAGE" | "CALC"

    // ✅ QueryDSL Projections.constructor 용 (7개)
    public Row(Long manageId,
               Long clubId,
               String clubNm,
               String clubLeader,
               Integer memberCnt,
               Integer supportAmount,
               String payYn) {
      this.manageId = manageId;
      this.clubId = clubId;
      this.clubNm = clubNm;
      this.clubLeader = clubLeader;
      this.memberCnt = memberCnt;
      this.supportAmount = supportAmount;
      this.payYn = payYn;
    }

    // ✅ 서비스에서 source까지 넣고 싶을 때 (8개)
    public Row(Long manageId,
               Long clubId,
               String clubNm,
               String clubLeader,
               Integer memberCnt,
               Integer supportAmount,
               String payYn,
               String source) {
      this(manageId, clubId, clubNm, clubLeader, memberCnt, supportAmount, payYn);
      this.source = source;
    }
  }

  // club/member 집계용 (계산 모드)
  @Getter @Setter @AllArgsConstructor
  public static class ClubMemberAgg {
    private Long clubId;
    private String clubNm;
    private String clubLeader;
    private Integer memberCnt;
  }

  // 규정 상세 DTO
  @Getter @Setter @AllArgsConstructor
  public static class RuleLine {
    private Long lineId;
    private Long applyId;
    private Integer memberCntFrom;
    private Integer memberCntTo;
    private Integer payAmount;
  }

  @Getter @Setter
  public static class SaveRequest {
    private String year;        // "2026"
    private List<SaveRow> rows;
  }

  @Getter @Setter
  public static class SaveRow {
    private Long clubId;
    private Integer clubMemberCnt;
    private Integer supportAmount;
    private String payYn; // Y/N
  }

  // 계산모드 select용
  @Getter @Setter
  public static class RowCalc {
    private Long clubId;
    private String clubNm;
    private String clubLeader;
    private Integer memberCnt;
    private Integer supportAmount;
    private String source; // ✅ 추가 (레포에서 "CALC"로 내려주는 값)

    public RowCalc(Long clubId, String clubNm, String clubLeader,
                   Integer memberCnt, Integer supportAmount, String source) {
      this.clubId = clubId;
      this.clubNm = clubNm;
      this.clubLeader = clubLeader;
      this.memberCnt = memberCnt;
      this.supportAmount = supportAmount;
      this.source = source;
    }
  }

}
