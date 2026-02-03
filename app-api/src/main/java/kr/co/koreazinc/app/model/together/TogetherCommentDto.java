package kr.co.koreazinc.app.model.together;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
public class TogetherCommentDto {
	
	private Long commentId;
	
    private Long boardId;
    
    private Long parentCommentId;
    
    private String content;
    
    private Integer recommendCnt;
    
    private String authorNm;
    
    private String authorPosition;
    
    private String createUser;
    
    private LocalDateTime createDate;
    
    private String updateUser;
    
    private LocalDateTime updateDate;
    
    private String deleteYn;
}