package kr.co.koreazinc.temp.model.entity.detail;

import org.hibernate.annotations.Comment;

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
@Table(name = "club_info")
@Entity
public class ClubDetail {
	@Id
	@Column(name = "club_id")
	@Comment("동호회 ID")
	private Integer cludId;
	
	@Column(name = "club_nm")
	@Comment("동호회명")
	private String clubName;
	
	@Column(name = "club_master_id")
    private String clubMasterId;

    @Column(name = "establish_dt")
    private String establishDt;

    @Column(name = "status")
    private String status;
    
    public static interface Setter {
    	public void setClubName(String clubName);
    	public void setDescription(String description);
    	public void setPresident(String president);
    	public void setEstablishedDate(String establishedDate);
    	public void setClubStatus(String clubStatus);
    	public void setMemberCnt(Long memberCnt);
    	public void setRequestCnt(Long requestCnt);
    	public void setRuleFileId(Integer ruleFileId);
    }
}
