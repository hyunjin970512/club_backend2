package kr.co.koreazinc.temp.model.entity.together;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "together_board")
@Entity
public class TogetherBoard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_id")
	private Long boardId;

	@Column(name = "club_code")
    private String clubCode;
	
	@Column(name = "together_code")
    private String togetherCode;
    
	@Column(name = "notice_dt")
    private String noticeDt;
    
	@Column(name = "title")
    private String title;
	
	@Column(name = "content")
    private String content;
	
	@Column(name = "view_cnt")
	private int viewCnt;
	    
    @Column(name = "recomend_cnt")
    private int recomendCnt;
    
    @Column(name = "delete_yn")
    private String deleteYn;
    
    @Column(name = "create_user")
    private String createUser;
    
    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
    
    /**
     * 게시글 수정
     */
    @Transactional
    public void update(String clubCode, String togetherCode, String title, String content, String noticeDt, String updateUser) {
    	this.clubCode = clubCode;
    	this.togetherCode = togetherCode;
    	this.title = title;
        this.content = content;
        this.noticeDt = noticeDt;
        this.updateUser = updateUser;
        this.updateDate = LocalDateTime.now();
    }
    
    /**
     * 게시글 삭제
     */
    @Transactional
    public void delete(String updateUser) {
    	this.deleteYn = "Y";
    	this.updateUser = updateUser;
    	this.updateDate = LocalDateTime.now();
    }
    
}
