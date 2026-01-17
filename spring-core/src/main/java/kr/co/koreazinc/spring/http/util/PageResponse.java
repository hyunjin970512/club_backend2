package kr.co.koreazinc.spring.http.util;

import java.util.Collection;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Schema(description = "페이지")
public class PageResponse<T> {

    @Schema(description = "데이터")
    private Collection<T> data;

    @Schema(description = "총 페이지 수", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalCount;

    public static <T> PageResponse<T> of(Collection<T> data) {
        return PageResponse.of(data, Long.valueOf(data.size()));
    }

    public static <T> PageResponse<T> of(Collection<T> data, Long totalCount) {
        return PageResponse.<T>builder()
            .data(data)
            .totalCount(totalCount)
            .build();
    }

    public Stream<T> stream() {
        return data.stream();
    }
}