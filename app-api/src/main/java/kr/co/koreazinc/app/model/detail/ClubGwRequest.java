package kr.co.koreazinc.app.model.detail;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClubGwRequest {
	private Long clubId;          // <ClubId>
    private Long requestId;       // <RequestId>
    private String clubName;      // <ClubName> / <RequestClub>
    private String purpose;       // <Purpose> (결성 취지 및 목적)
    private String memberCnt;     // <MemberCnt> (예: "30명")
    
    // 대표자(기안자) 정보
    private String requestNm;     // <RequestNm>
    private String deptCd;        // <MemberDept> 구성을 위함
    private String positionCd;    // <MemberDept> 구성을 위함
    private String companyNm;     // 회사명
    
    // 회칙 관련
    private MultipartFile ruleFile;    // 새 파일 수신용
    private Long ruleFileId;           // 기존 파일 ID
    private String ruleFileName;       // 파일명
    private String ruleFileUrl;        // 파일 링크

    // 회원 명부 관련
    private MultipartFile memberFile;  // 새 파일 수신용
    private Long memberFileId;           // 기존 파일 ID
    private String memberFileName;     // 파일명
    private String memberFileUrl;       // 파일 링크
}