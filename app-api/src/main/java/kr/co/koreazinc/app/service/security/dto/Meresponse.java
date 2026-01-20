package kr.co.koreazinc.app.service.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Meresponse {
  private String empNo;
  private String nameKo;
  private String coCd;
  private String deptCd;
  private String positionCd;
  private String cmpEmail;
  private String conEmail;
  private String proxyEmail;
}
