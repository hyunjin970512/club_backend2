package kr.co.koreazinc.app.model.account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.i18n.LocaleContextHolder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.koreazinc.data.model.attribute.enumeration.Yn;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import kr.co.koreazinc.data.model.embedded.piece.Use;
import kr.co.koreazinc.temp.model.entity.account.Account;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Get implements Account.Setter {

        @Schema(description = "계정 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private UUID accountId;

        @Schema(description = "사용자 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private String userId;

        @Schema(description = "사용자 패스워드")
        private String userPw;

        @Schema(description = "표기명")
        private String displayNm;

        @Schema(description = "계정명(다국어)")
        private I18N accountName;

        @Schema(description = "계정 설명")
        private String accountDesc;

        @Schema(description = "사용 조건", requiredMode = Schema.RequiredMode.REQUIRED)
        private Use use;

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

        @Schema(description = "계정명")
        public String getAccountNm() {
            return Optional.ofNullable(this.accountName).orElse(new I18N())
                    .getLocalName(LocaleContextHolder.getLocale());
        }

        @Schema(description = "생성자명")
        public Yn getUseYn() {
            return Optional.ofNullable(this.use).map(Use::getUseYn).orElse(Yn.N);
        }

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

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Post implements Account.Getter {

        @NotBlank
        @Schema(description = "사용자 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private String userId;

        @Schema(description = "사용자 패스워드")
        private String userPw;

        @Schema(description = "표기명")
        private String displayNm;

        @Schema(description = "계정 한글명")
        private String accountNameKo;

        @Schema(description = "계정 영문명")
        private String accountNameEn;

        @Schema(description = "계정 중문명")
        private String accountNameZh;

        @Schema(description = "계정 일문명")
        private String accountNameJa;

        @Schema(description = "계정 설명")
        private String accountDesc;

        @Builder.Default
        @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private Yn useYn = Yn.N;

        @Schema(description = "사용 시작일")
        private LocalDate useFrDt;

        @Schema(description = "사용 종료일")
        private LocalDate useToDt;

        @Override
        public I18N getAccountName() {
            return I18N.builder().ko(this.accountNameKo).en(this.accountNameEn)
                    .zh(this.accountNameZh).ja(this.accountNameJa).build();
        }

        @Override
        public Use getUse() {
            return Use.builder().yn(this.useYn).frDt(this.useFrDt).toDt(this.useToDt).build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Put implements Account.Getter {

        @NotNull
        @Schema(description = "계정 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private UUID accountId;

        @NotBlank
        @Schema(description = "사용자 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private String userId;

        @Schema(description = "사용자 패스워드")
        private String userPw;

        @Schema(description = "표기명")
        private String displayNm;

        @Schema(description = "계정 한글명")
        private String accountNameKo;

        @Schema(description = "계정 영문명")
        private String accountNameEn;

        @Schema(description = "계정 중문명")
        private String accountNameZh;

        @Schema(description = "계정 일문명")
        private String accountNameJa;

        @Schema(description = "계정 설명")
        private String accountDesc;

        @Builder.Default
        @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private Yn useYn = Yn.N;

        @Schema(description = "사용 시작일")
        private LocalDate useFrDt;

        @Schema(description = "사용 종료일")
        private LocalDate useToDt;

        @Override
        public I18N getAccountName() {
            return I18N.builder().ko(this.accountNameKo).en(this.accountNameEn)
                    .zh(this.accountNameZh).ja(this.accountNameJa).build();
        }

        @Override
        public Use getUse() {
            return Use.builder().yn(this.useYn).frDt(this.useFrDt).toDt(this.useToDt).build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Patch {

        @NotNull
        @Schema(description = "계정 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private UUID accountId;

        @Builder.Default
        @Schema(description = "사용자 아이디")
        private JsonNullable<String> userId = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "사용자 패스워드")
        private JsonNullable<String> userPw = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "표기명")
        private JsonNullable<String> displayNm = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "계정 한글명")
        private JsonNullable<String> accountNameKo = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "계정 영문명")
        private JsonNullable<String> accountNameEn = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "계정 중문명")
        private JsonNullable<String> accountNameZh = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "계정 일문명")
        private JsonNullable<String> accountNameJa = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "계정 설명")
        private JsonNullable<String> accountDesc = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "사용 여부")
        private JsonNullable<Yn> useYn = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "사용 시작일")
        private JsonNullable<LocalDate> useFrDt = JsonNullable.undefined();

        @Builder.Default
        @Schema(description = "사용 종료일")
        private JsonNullable<LocalDate> useToDt = JsonNullable.undefined();

        public UUID getKey() {
            return this.accountId;
        }

        public AccountDto.Put of(Account origin) {
            return Put.builder().accountId(this.accountId)
                    .userId(this.userId.orElse(origin.getUserId()))
                    .userPw(this.userPw.orElse(origin.getUserPw()))
                    .displayNm(this.displayNm.orElse(origin.getDisplayNm()))
                    .accountNameKo(this.accountNameKo.orElse(origin.getAccountName().getKo()))
                    .accountNameEn(this.accountNameEn.orElse(origin.getAccountName().getEn()))
                    .accountNameZh(this.accountNameZh.orElse(origin.getAccountName().getZh()))
                    .accountNameJa(this.accountNameJa.orElse(origin.getAccountName().getJa()))
                    .accountDesc(this.accountDesc.orElse(origin.getAccountDesc()))
                    .useYn(this.useYn.orElse(origin.getUse().getUseYn()))
                    .useFrDt(this.useFrDt.orElse(origin.getUse().getFrDt()))
                    .useToDt(this.useToDt.orElse(origin.getUse().getToDt())).build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema
    public static class Delete {

        @NotNull
        @Schema(description = "계정 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        private UUID accountId;

        public UUID getKey() {
            return this.accountId;
        }
    }
}
