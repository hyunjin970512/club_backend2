package kr.co.koreazinc.app.model.push;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppPushTokenUpsertRequest {

    @Schema(description = "사번", example = "S2021045")
    private String empNo;

    @Schema(description = "FCM 토큰", example = "xxxxx:APA91b...")
    private String token;

    @Schema(description = "단말 구분", example = "ANDROID")
    private String deviceType;

    @Schema(description = "앱/OS 정보", example = "Android 14 / App 1.0.3")
    private String userAgent;
}

