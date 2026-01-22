package kr.co.koreazinc.app.model.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubBoardDto {
	
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Get implements ClubBoard.Getter {
		private Integer noticeBoardId;
		private Integer boardId;
		private Integer clubId;
	    private String noticeTitle;
	    private String noticeContent;
	    private String title;
	    private String content;
	    private String authorNm;
	    private String authorPosition;
	    private String createUser;
	    private java.time.LocalDateTime createDate;
	    private String updateUser;
	    private java.time.LocalDateTime updateDate;
	    private int viewCnt;
	    private int recomendCnt;
	    private int commentCnt;
	    private String noticeYn;
	    private String deleteYn;
	    private String userEmpNo; 
	    private String isNotice; 
	    private String expiryDate;
	    private String noticeDt;
	}
	
	@Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Delete implements ClubBoard.Getter {
		@NotNull
        @Schema(description = "게시글 번호", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer boardId;
        
        @Schema(description = "문서 번호 (첨부파일 그룹)")
        private String boardDocNo;

        @Schema(description = "삭제 수행자 사번")
        private String userEmpNo;

        @Override public Integer getBoardId() { return this.boardId; }
        @Override public String getUserEmpNo() { return this.userEmpNo; }
	}
	
	@Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Update implements ClubBoard.Getter {
		private Integer boardId;
	    private Integer clubId;
	    private String title;
	    private String content;
	    private String expiryDate; 
	    private String isNotice;   
	    private String userEmpNo;
	    
	    @Override public String getIsNotice() { return isNotice; }
	    @Override public String getExpiryDate() { return expiryDate; }
	    @Override public String getUserEmpNo() { return userEmpNo; }
	}
}