package kr.co.koreazinc.app.model.detail;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.koreazinc.app.model.account.AccountDto;
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
	    private int recommendCnt;
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

        @Override
		public Integer getClubId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTitle() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getNoticeYn() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getNoticeDt() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDeleteYn() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCreateUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocalDateTime getCreateDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getUpdateUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocalDateTime getUpdateDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getViewCnt() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getRecommendCnt() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getIsNotice() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getExpiryDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Schema(description = "작성자 사번")
        private String userEmpNo;
	}
}