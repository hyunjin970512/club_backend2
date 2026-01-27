package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "club_user_info",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_club_user",
            columnNames = { "club_id", "emp_no" }
        )
    }
)
@Getter
@NoArgsConstructor
public class ClubUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_user_id")
    private Long clubUserId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Column(name = "emp_no", nullable = false, length = 255)
    private String empNo;

    @Column(name = "user_role_cd", length = 20)
    private String userRoleCd;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
    
    @Builder
    public ClubUserInfo(Long clubId, String empNo, String userRoleCd, String status, String createUser) {
    	this.clubId = clubId;
        this.empNo = empNo;
        this.userRoleCd = userRoleCd;
        this.joinDate = LocalDate.now();
        this.status = status;
        this.createUser = createUser;
        this.createDate = LocalDateTime.now();
        this.updateUser = createUser;
        this.updateDate = LocalDateTime.now();
    }
}
