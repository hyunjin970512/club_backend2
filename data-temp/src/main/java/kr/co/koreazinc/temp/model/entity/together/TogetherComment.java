package kr.co.koreazinc.temp.model.entity.together;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "together_comment")
@Entity
public class TogetherComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

	// TogetherBoard 객체로 맵핑
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private TogetherBoard boardId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "content")
    private String content;

    @Column(name = "recomend_cnt")
    private Integer recomendCnt;

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
     * 댓글 수정
     */
    @Transactional
    public void updateComment(String content, String userEmpNo) {
    	this.content = content;
    	this.updateUser = userEmpNo;
    	this.updateDate = LocalDateTime.now();
    }
    

    /**
     * 댓글 삭제
     */
    public void deleteComment(String userEmpNo) {
    	this.deleteYn = "Y";
    	this.updateUser = userEmpNo;
    	this.updateDate = LocalDateTime.now();
    }
}
