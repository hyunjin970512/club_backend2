package kr.co.koreazinc.temp.model.entity.detail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "club_comment", schema = "public")
@Entity
public class ClubComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

	// ClubBoard 객체로 맵핑
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private ClubBoard clubBoard;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "content")
    private String content;

    @Column(name = "recomend_cnt")
    private Integer recommendCnt;

    @Column(name = "delete_yn")
    private String deleteYn;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_date")
    private java.time.LocalDateTime createDate;

    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private java.time.LocalDateTime updateDate;

    public interface Setter {
        void setContent(String content);
        void setDeleteYn(String deleteYn);
        void setRecommendCnt(Integer recommendCnt);
    }
    
    public void softDelete(String userEmpNo) {
    	this.deleteYn = "Y";
    	this.updateUser = userEmpNo;
    	this.updateDate = java.time.LocalDateTime.now();
    }
}
