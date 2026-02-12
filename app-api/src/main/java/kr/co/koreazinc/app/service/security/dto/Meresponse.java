package kr.co.koreazinc.app.service.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Meresponse {
  private String empNo;
  private String nameKo;
  private String coCd;
  private String deptCd;
  private String positionCd;
  private String cmpEmail;
  private String conEmail;
  private String proxyEmail;
  private List<Long> joinedClubIds;
  private List<Long> joinRequestClubIds;
}
