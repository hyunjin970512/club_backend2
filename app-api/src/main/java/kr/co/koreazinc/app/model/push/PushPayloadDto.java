package kr.co.koreazinc.app.model.push;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PushPayloadDto {
  private final String title;
  private final String body;
  private final String linkUrl;
  private final String payloadJson; // SW랑 DB용
}
