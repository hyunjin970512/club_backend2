package kr.co.koreazinc.data.model.embedded.piece;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import org.hibernate.annotations.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import kr.co.koreazinc.data.model.attribute.enumeration.Yn;
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
@Embeddable public class Use implements Serializable {

    @Builder.Default
    @Comment("사용 여부")
    @Schema(description = "사용 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Yn yn = Yn.N;

    @Comment("사용 시작일")
    @Schema(description = "사용 시작일")
    private LocalDate frDt;

    @Comment("사용 종료일")
    @Schema(description = "사용 종료일")
    private LocalDate toDt;

    public Yn getUseYn() {
        return Yn.ofBoolean(Yn.Y.equals(this.yn) && isAfter() && isBefore());
    }

    private boolean isAfter() {
        return LocalDate.now().isAfter(Optional.ofNullable(frDt).orElse(LocalDate.MIN));
    }

    private boolean isBefore() {
        return LocalDate.now().isBefore(Optional.ofNullable(toDt).orElse(LocalDate.MAX));
    }
}