package kr.co.koreazinc.app.model.main;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClubMemberDto {
	private String memberEmpNo;
    private LocalDate joinDate;
    private String deptNm;
    private String memberNm;
    private String positionCd;
    private String companyNm;
    private String empNo;
}
