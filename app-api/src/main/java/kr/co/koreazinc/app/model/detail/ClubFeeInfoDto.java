package kr.co.koreazinc.app.model.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.koreazinc.temp.model.entity.detail.ClubFeeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubFeeInfoDto {
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Get implements ClubFeeInfo.Setter {
		private Integer feeId;
		private Integer clubId;
		private String positionCd;
		private String positionNm;
		private Integer positionAmt;
		private String createUser;
		private java.time.LocalDateTime createDate;
		private String updateUser;
		private java.time.LocalDateTime updateDate;
	}
}