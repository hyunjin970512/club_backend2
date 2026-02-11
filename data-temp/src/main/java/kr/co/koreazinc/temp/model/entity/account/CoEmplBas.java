package kr.co.koreazinc.temp.model.entity.account;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "cmp_email")
    private String cmpEmail;

    @Column(name = "con_email")
    private String conEmail;

    @Column(name = "proxy_email")
    private String proxyEmail;

    @Column(name = "pwd", nullable = false)
    private String pwd;

    @Column(name = "name_ko", nullable = false)
    private String nameKo;

    @Column(name = "use_at", nullable = false)
    private String useAt;

    @Column(name = "delete_at", nullable = false)
    private String deleteAt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "empl_role_cd", nullable = false)
    private String emplRoleCd;

    @Column(name = "user_id")
    private String userId;
}
