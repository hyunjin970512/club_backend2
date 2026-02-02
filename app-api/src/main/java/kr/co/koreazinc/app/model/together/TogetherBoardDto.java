package kr.co.koreazinc.app.model.together;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TogetherBoardDto {
	
	private Long boardId;
	
	private String clubCode;
	
    private String togetherCode;
    
    private String title;
    
    private String content;
    
    private String noticeDt;
    
    private Integer viewCnt;
    
    private Integer recomendCnt;
    
    private String deleteYn;
    
    private String createUser;
    
    private LocalDateTime createDate;
    
    private String updateUser;
    
    private LocalDateTime updateDate;
    
    private List<Long> existFileId;   // 수정 시 유지할 기존 파일 ID 목록
}