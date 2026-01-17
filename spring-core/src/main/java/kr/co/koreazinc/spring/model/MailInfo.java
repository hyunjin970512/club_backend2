package kr.co.koreazinc.spring.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailInfo {

    @Schema(description = "발송자 이메일")
    private String sender;

    @Schema(description = "발신자 이메일")
    private String from;

    @Builder.Default
    @Schema(description = "수신자 이메일")
    private Set<String> to = new HashSet<>();

    @Builder.Default
    @Schema(description = "참조자 이메일")
    private Set<String> cc = new HashSet<>();

    @Builder.Default
    @Schema(description = "숨은 참조자 이메일")
    private Set<String> bcc = new HashSet<>();

    @Schema(description = "제목")
    private String subject;

    @Schema(description = "내용")
    private String content;

    @Builder.Default
    @Schema(description = "첨부파일")
    private Set<File> attachments = new HashSet<>();

    public boolean hasAttachment() {
        return !this.attachments.isEmpty();
    }

    public String getSender() {
        if (this.sender == null) return this.from;
        return this.sender;
    }
}