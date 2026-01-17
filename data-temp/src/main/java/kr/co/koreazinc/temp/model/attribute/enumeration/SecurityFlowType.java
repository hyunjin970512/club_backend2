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
public enum SecurityFlowType implements BaseEnum {
    $("@", I18N.builder().ko("보안 승인 방식 종류").en("Security Flow Type").zh("安全授权方式").ja("セキュリティフロータイプ")
            .build()), IMPLICIT(
                    "implicit",
                    I18N.builder().ko("암묵적 승인 방식").en("Implicit Grant").zh("隐式授权").ja(
                            "暗黙の許可").build()), PASSWORD("password", I18N.builder()
                                    .ko("자원 소유자 자격 증명 방식").en("Resource Owner Password Credentials")
                                    .zh("资源所有者密码凭据").ja("リソース所有者パスワード資格情報")
                                    .build()), CLIENT_CREDENTIALS(
                                            "clientCredentials",
                                            I18N.builder().ko("클라이언트 자격 증명 방식")
                                                    .en("Client Credentials").zh("客户端凭据")
                                                    .ja("クライアント資格情報").build()), AUTHORIZATION_CODE(
                                                            "authorizationCode",
                                                            I18N.builder().ko("권한 부여 승인 코드 방식")
                                                                    .en("Authorization Code")
                                                                    .zh("授权码").ja("認可コード").build());

    @JsonValue
    private String value;

    private I18N name;

    public static SecurityFlowType of(String value) {
        for (SecurityFlowType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
