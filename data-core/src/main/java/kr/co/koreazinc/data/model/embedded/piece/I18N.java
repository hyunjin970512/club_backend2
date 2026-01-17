package kr.co.koreazinc.data.model.embedded.piece;

import java.io.Serializable;
import java.util.Locale;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable public class I18N implements Serializable {

    @Comment("한글명")
    @Schema(description = "한글명")
    private String ko;

    @Comment("영문명")
    @Schema(description = "영문명")
    private String en;

    @Comment("중문명")
    @Schema(description = "중문명")
    private String zh;

    @Comment("일문명")
    @Schema(description = "일문명")
    private String ja;

    public String getLocalName(Locale locale) {
        if (Locale.KOREAN.equals(locale)) return this.ko;
        if (Locale.ENGLISH.equals(locale)) return this.en;
        if (Locale.CHINESE.equals(locale)) return this.zh;
        if (Locale.JAPANESE.equals(locale)) return this.ja;
        return this.ko;
    }
}