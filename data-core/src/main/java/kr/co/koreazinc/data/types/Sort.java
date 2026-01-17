package kr.co.koreazinc.data.types;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Sort {

    @Schema(description = "정렬 기준 필드명")
    private String selector;

    @Builder.Default
    @Schema(description = "정렬 방향 (true: 내림차순, false: 오름차순)")
    private boolean desc = false;

    public boolean isDesc() {
        return this.desc;
    }
}