package kr.co.koreazinc.app.model.push;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppPushPayloadDto {
        private final String title;
        private final String content;
        private final String payloadJson;
}
