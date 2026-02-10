package kr.co.koreazinc.app.model.push;

import java.time.LocalDateTime;

import lombok.Getter;
import kr.co.koreazinc.temp.model.entity.push.PushInbox;

@Getter
public class InboxItemDto {

    private final Long inboxId;
    private final Boolean read;
    private final LocalDateTime createdAt;

    private final String eventType;
    private final String title;
    private final String body;
    private final String linkUrl;
    private final String payloadJson;

    // ✅ QueryDSL Projections.constructor 매칭용 (필수)
    public InboxItemDto(
            Long inboxId,
            String readYn,
            LocalDateTime createdAt,
            String eventType,
            String title,
            String body,
            String linkUrl,
            String payloadJson
    ) {
        this.inboxId = inboxId;
        this.read = PushInbox.READ_Y.equals(readYn);
        this.createdAt = createdAt;
        this.eventType = eventType;
        this.title = title;
        this.body = body;
        this.linkUrl = linkUrl;
        this.payloadJson = payloadJson;
    }
}
