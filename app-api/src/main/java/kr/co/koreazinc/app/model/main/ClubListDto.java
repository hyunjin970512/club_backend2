package kr.co.koreazinc.app.model.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubListDto {

    private Long clubId;      // 동호회 ID
    private String clubNm;    // 동호회명
    private String clubDesc;  // 동호회 설명
    private String clubType;  // 동호회 타입 코드 (sub_code)
    private String clubTypeNm;// 동호회 타입명 (code_nm)
    private Long joinCnt;     // 가입자 수
}
