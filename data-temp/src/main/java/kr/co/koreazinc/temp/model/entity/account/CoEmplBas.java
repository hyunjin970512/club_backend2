package kr.co.koreazinc.temp.model.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "co_empl_bas", schema = "public")
@Entity
public class CoEmplBas {
	@Id
    @Column(name = "emp_no", nullable = false)
    private String empNo;

    @Column(name = "co_cd")
    private String coCd;

    @Column(name = "dept_cd")
    private String deptCd;

    @Column(name = "position_cd")
    private String positionCd;

    @Column(name = "name_ko")
    private String nameKo;

    @Column(name = "use_at")
    private String useAt;

    @Column(name = "delete_at")
    private String deleteAt;

    @Column(name = "delete_dt")
    private String deleteDt;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "empl_role_cd", nullable = false)
    private String emplRoleCd;
}
