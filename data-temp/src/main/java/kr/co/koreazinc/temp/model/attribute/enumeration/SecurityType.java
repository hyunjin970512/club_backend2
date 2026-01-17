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
public enum SecurityType implements BaseEnum {
    $("@", I18N.builder().ko("보안 종류").en("Security Type").zh("").ja("").build()), APIKEY("apiKey",
            I18N.builder().ko("API 키").en("API Key").zh("API 密钥").ja("API キー").build()), HTTP(
                    "http",
                    I18N.builder().ko("HTTP").en("HTTP").zh("HTTP").ja("HTTP").build()), OAUTH2(
                            "oauth2",
                            I18N.builder().ko("OAuth 2.0").en("OAuth 2.0").zh("OAuth 2.0")
                                    .ja("OAuth 2.0").build()), OPENIDCONNECT(
                                            "openIdConnect",
                                            I18N.builder().ko("OpenID Connect").en("OpenID Connect")
                                                    .zh("OpenID Connect").ja("OpenID Connect")
                                                    .build()), MUTUALTLS(
                                                            "mutualTLS",
                                                            I18N.builder().ko("상호 TLS")
                                                                    .en("Mutual TLS").zh("相互 TLS")
                                                                    .ja("相互 TLS").build());

    @JsonValue
    private String value;

    private I18N name;

    public static SecurityType of(String value) {
        for (SecurityType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
