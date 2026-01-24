package kr.co.koreazinc.app.model.detail;

import java.time.LocalDateTime;
import kr.co.koreazinc.temp.model.entity.detail.ClubComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
public class ClubCommentDto implements ClubComment.Getter {
	private Long commentId;
    private Integer boardId;
    private Long parentCommentId;
    private String content;
    private Integer recommendCnt;
    private String authorNm;
    private String authorPosition;
    private String createUser;
    private LocalDateTime createDate;
    private String updateUser;
    private LocalDateTime updateDate;
}