package kr.co.koreazinc.app.model.auth;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SsoUserDto {
  private String userId;
  private String userNm;
  private String userKoNm;
  private String email;
  private String useYn;
  private List<JobDto> job;

  @Getter @Setter
  public static class JobDto {
    private String bassYn;   // "Y"
    private String useYn;    // "Y"
    private String empNo;    // "S2021045"
    private String empId;
    private String empStatus;
    private String deptCd;
    private String deptNm;
    private String coCd;
    private String coNm;
    private String bsCd;
    private String bsNm;
    private String posCd;
    private String posNm;
    private String rankCd;
    private String rankNm;
    private String ttlCd;
    private String ttlNm;
    private String ecnyDt;
    private String retireDt;
    private String userKoNm;
  }

  public JobDto primaryJobOrThrow() {
    if (job == null || job.isEmpty()) throw new IllegalStateException("SSO job empty");
    return job.stream()
      .filter(j -> "Y".equalsIgnoreCase(j.getBassYn()))
      .findFirst()
      .orElse(job.get(0));
  }
}
