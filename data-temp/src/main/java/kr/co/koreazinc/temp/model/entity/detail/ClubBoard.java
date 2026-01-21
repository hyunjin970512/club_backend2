package kr.co.koreazinc.temp.model.entity.detail;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "club_board")
@Entity
public class ClubBoard{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Integer boardId;
	
	@Column(name = "club_id")
	private Integer clubId;
	
	@Column(name = "notice_yn")
    private String noticeYn;
	
	@Column(name = "notice_dt")
    private String noticeDt;
	
	@Column(name = "delete_yn")
    private String deleteYn;
	
	@Column(name = "title")
    private String title;
	
	@Column(name = "content")
    private String content;
	
	@Transient
    private String authorNm;
	
	@Transient
    private String authorPosition;
    
    @Column(name = "create_user")
    private String createUser;
    
    @Column(name = "create_date")
    private java.time.LocalDateTime createDate;
    
    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private java.time.LocalDateTime updateDate;
    
    @Column(name = "view_cnt")
    private int viewCnt;
    
    @Column(name = "recomend_cnt")
    private int recommendCnt;
    
    @Transient
    private int commentCnt;
    
    public interface Getter {
        Integer getBoardId();
        Integer getClubId();
        String getTitle();
        String getContent();
        String getNoticeYn();
        String getNoticeDt();
        String getDeleteYn();
        String getCreateUser();
        java.time.LocalDateTime getCreateDate();
        String getUpdateUser();
        java.time.LocalDateTime getUpdateDate();
        int getViewCnt();
        int getRecommendCnt();
        String getUserEmpNo(); 
        String getIsNotice();
        String getExpiryDate();
    }
    
    public interface Setter {
    	public void setClubId(Integer clubId);
    	public void setNoticeBoardId(Integer noticeBoardId);
    	public void setBoardId(Integer boardId);
    	public void setNoticeTitle(String noticeTitle);
    	public void setNoticeContent(String noticeContent);
        public void setTitle(String title);
        public void setContent(String content);
        public void setAuthorNm(String authorNm);
        public void setAuthorPosition(String authorPosition);
        public void setCreateUser(String createUser);
        public void setCreateDate(java.time.LocalDateTime createDate);
        public void setUpdateUser(String updateUser);
        public void setUpdateDate(java.time.LocalDateTime updateDate);
        public void setViewCnt(int viewCnt);
        public void setRecommendCnt(int recommendCnt);
        public void setCommentCnt(int commentCnt);
        public void setNoticeYn(String noticeYn);
        public void setDeleteYn(String deleteYn);
    }
    
    @OneToMany(mappedBy = "clubBoard", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClubComment> comments = new ArrayList<>();
    
    // 삭제 비지니스 로직
    public void deletePost(String updateUser) {
    	this.deleteYn = "Y";
        this.updateUser = updateUser;
        this.updateDate = java.time.LocalDateTime.now();
        
        // 게시글에 달린 댓글들도 모두 Soft Delete 처리
        if (this.comments != null) {
            this.comments.forEach(c -> c.softDelete(updateUser));
        }
    }
}
