package kr.co.koreazinc.app.model.together;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubBoardDto.FileDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    
    private String authorNm;
    
    private String authorPosition;
    
    @Schema(description = "첨부파일 목록")
    private List<FileDto> files;
    
    @Schema(description = "존재 첨부파일 목록")
    private List<Long> existFileId;
    
    /**
     * 첨부파일 정보를 담기 위한 DTO
     */
	@Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema(description = "첨부파일 상세 정보")
	public static class FileDto {
		@Schema(description = "문서 번호")
        private Long docNo;
        
        @Schema(description = "원본 파일명")
        private String docFileNm;
        
        @Schema(description = "이미지 미리보기/보기 URL")
        private String displayUrl;
        
        @Schema(description = "파일 다운로드 URL")
        private String downloadUrl;
        
        @Schema(description = "파일 확장자")
        private String fileExt;
        
        @Schema(description = "파일 크기")
        private Long fileSize;
	}
}