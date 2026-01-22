package kr.co.koreazinc.app.model.detail;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
public class ClubCommentDto {
	private Long commentId;
    private Integer boardId;
    private Long parentCommentId;
    private String content;
    private Integer recommendCnt;
    private String authorNm;
    private String authorPosition;
    private LocalDateTime createDate;
}