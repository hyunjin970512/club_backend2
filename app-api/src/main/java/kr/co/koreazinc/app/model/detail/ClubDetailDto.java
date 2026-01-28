package kr.co.koreazinc.app.model.detail;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.koreazinc.temp.model.entity.detail.ClubDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubDetailDto {
	
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Get implements ClubDetail.Setter {
		// 동호회 기본 정보
		private Integer clubId;
		private String clubName;
        private String description;
        private String president;
        private String establishedDate;
        private String clubStatus;
        private Long memberCnt;
        private Long requestCnt;
        private Integer ruleFileId;
        private Integer requestId;
        private String companyNm;
        private String positionCd;
        private String requestNm;
        private String purpose;
        private String deptCd;
        private String docFileNm;
        private String downloadUrl;
        
        private List<ClubBoardDto.Get> notices;
	}
}
