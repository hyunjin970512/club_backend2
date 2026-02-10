package kr.co.koreazinc.spring.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PushInfo {

    @Schema(description = "프로젝트 아이디")
    private String projectId;

    @Schema(description = "푸시 토큰")
    private String token;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;
}
