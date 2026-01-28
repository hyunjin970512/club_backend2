package kr.co.koreazinc.temp.model.chk;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "co_empl_bas", schema = "public")
public class Employee {

  @Id
  @Column(name = "emp_no")
  private String empNo;

  @Column(name = "name_ko")
  private String nameKo;
  
  @Column(name = "user_id")
  private String userId;

  @Column(name = "co_cd")
  private String coCd;

  @Column(name = "dept_cd")
  private String deptCd;

  @Column(name = "position_cd")
  private String positionCd;

  @Column(name = "cmp_email")
  private String cmpEmail;

  @Column(name = "con_email")
  private String conEmail;

  @Column(name = "proxy_email")
  private String proxyEmail;

  @Column(name = "use_at")
  private String useAt;

  @Column(name = "delete_at")
  private String deleteAt;

  @Column(name = "empl_role_cd")
  private String emplRoleCd;
}
