package kr.co.koreazinc.temp.model.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "club_apply_fee_manage",
    uniqueConstraints = @UniqueConstraint(name = "uk_fee_manage", columnNames = {"club_id","year"}))
public class ClubApplyFeeManage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "manage_id")
	private Long manageId;
	
	@Column(name = "club_id", nullable = false)
	private Long clubId;
	
	@Column(name = "year", length = 4)
	private String year;
	
	@Column(name = "club_member_cnt", nullable = false)
	private Integer clubMemberCnt;
	
	@Column(name = "support_amount", nullable = false)
	private Integer supportAmount;
	
	@Column(name = "pay_yn", length = 1, nullable = false)
	private String payYn;
	
	@Column(name = "create_user")
	private String createUser;
	
	@Column(name = "create_date")
	private LocalDateTime createDate;
	
	@Column(name = "update_user")
	private String updateUser;
	
	@Column(name = "update_date")
	private LocalDateTime updateDate;
	
	@PrePersist
    void onCreate() {
        if (createDate == null) createDate = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}
