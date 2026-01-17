package kr.co.koreazinc.spring.security.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 정보")
public class UserInfo {

    @Schema(description = "사용자 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    protected String userId;

    @Schema(description = "사용자 표기명")
    protected String userNm;

    @Schema(description = "사용자 한글명")
    protected String userKoNm;

    @Schema(description = "사용자 영문명")
    protected String userEnNm;

    @Schema(description = "사용자 중문명")
    protected String userZhNm;

    @Schema(description = "사용자 일문명")
    protected String userJaNm;

    @Schema(description = "메일 주소")
    protected String email;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "직무 목록")
    // protected Collection<UserJobDto> job;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "라이선스 목록")
    // protected Collection<UserLicenseDto> license;

    @Schema(description = "사용 여부", allowableValues = "Y, N", requiredMode = Schema.RequiredMode.REQUIRED)
    protected String useYn;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "사용 시작일")
    protected LocalDate useFrDt;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "사용 종료일")
    protected LocalDate useToDt;
}