package kr.co.koreazinc.app.model.account.view;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.i18n.LocaleContextHolder;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import kr.co.koreazinc.temp.model.attribute.enumeration.MappingType;
import kr.co.koreazinc.temp.model.entity.account.view.VAccount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VAccountDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Get implements VAccount.Setter {

        @Schema(description = "고유 아이디")
        private UUID id;

        @Schema(description = "매핑 아이디")
        private UUID mappingId;

        @Schema(description = "매핑 유형")
        private MappingType mappingTp;

        @Schema(description = "그룹 아이디")
        private UUID groupId;

        @Schema(description = "그룹 코드")
        private String groupCd;

        @Schema(description = "계정 아이디")
        private UUID accountId;

        @Schema(description = "사용자 아이디")
        private String userId;

        @Schema(description = "어플리케이션 아이디")
        private UUID applicationId;

        @Schema(description = "어플리케이션 코드")
        private String applicationCd;

        @Schema(description = "라이선스 아이디")
        private UUID licenseId;

        @Schema(description = "라이선스 코드")
        private String licenseCd;

        @Schema(description = "생성자")
        private UUID createId;

        @Schema(description = "수정자(다국어)")
        private I18N createName;

        @Schema(description = "생성일")
        private LocalDateTime createAt;

        @Schema(description = "수정자")
        private UUID updateId;

        @Schema(description = "수정자(다국어)")
        private I18N updateName;

        @Schema(description = "수정일")
        private LocalDateTime updateAt;

        @Schema(description = "생성자명")
        public String getCreateNm() {
            return Optional.ofNullable(this.createName).orElse(new I18N())
                    .getLocalName(LocaleContextHolder.getLocale());
        }

        @Schema(description = "수정자명")
        public String getUpdateNm() {
            return Optional.ofNullable(this.updateName).orElse(new I18N())
                    .getLocalName(LocaleContextHolder.getLocale());
        }
    }
}
