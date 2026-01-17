package kr.co.koreazinc.spring.util;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "OAuth 인증 정보")
public class OAuth {

    @Schema(description = "토큰 유형")
    String tokenType;

    @Schema(description = "유효 기간(초)")
    Integer expiresIn;

    @Schema(description = "요청된 액세스 토큰")
    String accessToken;

    @Schema(description = "오류 코드 문자열")
    String error;

    @Schema(description = "오류 메시지")
    String errorDescription;

    @Schema(description = "오류 코드 목록")
    List<Integer> errorCodes;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss'Z'")
    @Schema(description = "오류 발생 시간")
    LocalDateTime timestamp;

    @Schema(description = "요청 고유 식별자")
    String traceId;

    @Schema(description = "구성 고유 식별자")
    String correlationId;

    @Schema(description = "오류 참고 페이지")
    String errorUri;

    @JsonIgnore
    public boolean isError() {
        return StringUtils.isNotBlank(error);
    }
}