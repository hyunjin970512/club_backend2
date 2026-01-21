package kr.co.koreazinc.temp.model.entity.main;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "co_empl_bas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoEmplBas {

    @Id
    @Column(name = "emp_no", length = 255)
    private String empNo;   // 사번 (PK)

    @Column(name = "co_cd", length = 255)
    private String coCd;

    @Column(name = "dept_cd", length = 255)
    private String deptCd;

    @Column(name = "position_cd", length = 255)
    private String positionCd;

    @Column(name = "cmp_email", length = 255)
    private String cmpEmail;

    @Column(name = "con_email", length = 255)
    private String conEmail;

    @Column(name = "proxy_email", length = 255)
    private String proxyEmail;

    @Column(name = "pwd", nullable = false, length = 255)
    private String pwd;

    @Column(name = "name_ko", nullable = false, length = 255)
    private String nameKo;

    @Column(name = "use_at", nullable = false, length = 1)
    private String useAt;       // 'Y' / 'N'

    @Column(name = "delete_at", nullable = false, length = 1)
    private String deleteAt;    // 'Y' / 'N'

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @Column(name = "create_user", length = 255)
    private String createUser;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 255)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "empl_role_cd", nullable = false, length = 255)
    private String emplRoleCd;   // 권한 코드 (menu_role_map.role_cd랑 매칭)
}
