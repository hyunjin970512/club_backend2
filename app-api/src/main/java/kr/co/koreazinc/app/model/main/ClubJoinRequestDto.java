package kr.co.koreazinc.app.model.main;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClubJoinRequestDto {
	private String requestEmpNo;
    private String requestNm;
    private String deptNm;
    private String positionCd;
    private String companyNm;
}
