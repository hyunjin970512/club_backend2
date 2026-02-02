package kr.co.koreazinc.app.model.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TogetherBoardListDto {

    private Long boardId;

    private String createUser;
    private String nameKo;     // 작성자명

    private String siteType;   // club_code
    private String siteName;   // CLUB_TYPE code_nm

    private String title;

    private String type;       // together_code
    private String typeName;   // TOGETHER_TYPE code_nm

    private Integer viewCnt;
    private Integer recomendCnt;
}
