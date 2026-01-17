package kr.co.koreazinc.temp.model.attribute.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.data.model.attribute.BaseEnum;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotifyLevel implements BaseEnum {
    $("@", I18N.builder().ko("알람 중요도").en("Notification Level").zh("").ja("").build()), DEBUG(
            "DEBUG", I18N.builder().ko("디버그").en("Debug").zh("").ja("").build()), INFO("INFO",
                    I18N.builder().ko("정보").en("Info").zh("").ja("").build()), WARN("WARN",
                            I18N.builder().ko("경고").en("Warn").zh("").ja("").build()), ERROR(
                                    "ERROR",
                                    I18N.builder().ko("오류").en("Error").zh("").ja("")
                                            .build()), FATAL("FATAL",
                                                    I18N.builder().ko("치명적").en("Fatal").zh("")
                                                            .ja("").build()),;

    @JsonValue
    private String value;

    private I18N name;

    public static NotifyLevel of(String value) {
        for (NotifyLevel type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
