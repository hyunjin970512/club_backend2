package kr.co.koreazinc.app.model.push;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InboxItemDto {

	private Long inboxId;
	private Boolean read;
	private LocalDateTime createdAt;
	
	private String eventType;
	private String title;
	private String body;
	private String linkUrl;
	private String payloadJson;
}
