package kr.co.koreazinc.temp.model.entity.detail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	    name = "club_fee_info",
	    schema = "public",
	    uniqueConstraints = @UniqueConstraint(name = "uk_club_fee", columnNames = {"club_id", "position_cd"})
	)
@Entity
public class ClubFeeInfo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ 이게 핵심
    @Column(name = "fee_id")
    private Integer feeId;
	
    @Column(name = "club_id", nullable = false)
    private Integer clubId;

    @Column(name = "position_cd")
    private String positionCd;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_date")
    private java.time.LocalDateTime createDate;

    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private java.time.LocalDateTime updateDate;
    
    public interface Setter {
        void setPositionAmt(Integer amount);
        void setUpdateUser(String updateUser);
        void setUpdateDate(java.time.LocalDateTime updateDate);
    }
}
