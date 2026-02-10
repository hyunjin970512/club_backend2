package kr.co.koreazinc.app.model.push;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppPushSendRequest {

    @Schema(description = "수신자 사번", example = "S2021045")
    private String empNo;

    @Schema(description = "제목", example = "전송테스트")
    private String title;

    @Schema(description = "내용", example = "push 알림 테스트입니다")
    private String content;
}
