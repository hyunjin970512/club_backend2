package kr.co.koreazinc.spring.security.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema
public class ResponseToken {

    /**  Successful  **/

    @Schema(description = "토큰 유형")
    String tokenType;

    @Schema(description = "액세스 토큰 유효 기간(초)")
    Integer accessTokenExpiresIn;

    @Schema(description = "액세스 토큰")
    String accessToken;

    @Schema(description = "리프레쉬 토큰 유효 기간(초)")
    Integer refreshTokenExpiresIn;

    @Schema(description = "리프레쉬 토큰")
    String refreshToken;

    /**  Error  **/

    @Schema(description = "오류 코드")
    String error;

    @Schema(description = "오류 메시지")
    String errorDescription;

    public ResponseToken(String tokenType, Integer accessTokenExpiresIn, String accessToken, Integer refreshTokenExpiresIn, String refreshToken) {
        this(tokenType, accessTokenExpiresIn, accessToken, refreshTokenExpiresIn, refreshToken, null, null);
    }

    @JsonIgnore
    public boolean isBlank() {
        return StringUtils.isBlank(accessToken);
    }

    @JsonIgnore
    public boolean isNotBlank() {
        return !isBlank();
    }
}