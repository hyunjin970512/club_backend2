package kr.co.koreazinc.temp.model.entity.account;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "co_empl_bas", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Employee {

	@Id
	@Column(name = "emp_no")
	private String empNo;
	
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
	
	@Column(name = "name_ko")
	private String nameKo;
	
	@Column(name = "empl_role_cd")
	private String emplRoleCd;
	
	@Column(name = "use_at")
	private String useAt;
	
	@Column(name = "delete_at")
	private String deleteAt;
	
	@Column(name = "delete_dt")
	private LocalDateTime deleteDt;
	
	@Column(name = "create_user")
	private String createUser;
	
	@Column(name = "create_date")
	private LocalDateTime createDate;
	
	@Column(name = "update_user")
	private String updateUser;
	
	@Column(name = "update_date")
	private LocalDateTime updateDate;
	
	@JsonIgnore // 혹시 엔티티를 실수로 내려도 막기
	@Column(name = "pwd")
	private String pwd;
	  
}
