package kr.co.koreazinc.temp.model.entity.account;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import kr.co.koreazinc.data.model.embedded.piece.Use;
import kr.co.koreazinc.data.support.DataType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Account",
        indexes = {@Index(name = "IDX_User_ID", columnList = "User_ID", unique = true),
                @Index(name = "IDX_Account_Use_YN", columnList = "Use_YN")})
@Entity
public class Account {

    @Id
    @Comment("계정 아이디")
    @JdbcTypeCode(Types.NVARCHAR)
    @Column(name = "Account_ID", columnDefinition = DataType.NVARCHAR, length = 36)
    private UUID accountId;

    @Comment("사용자 아이디")
    @Column(name = "User_ID", columnDefinition = DataType.NVARCHAR, length = 100, nullable = false)
    private String userId;

    @Comment("사용자 패스워드")
    @Column(name = "User_PW", columnDefinition = DataType.NVARCHAR)
    private String userPw;

    @Comment("표기명")
    @Column(name = "Display_NM", columnDefinition = DataType.NVARCHAR)
    private String displayNm;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ko",
                    column = @Column(name = "Name_KO", columnDefinition = DataType.NVARCHAR)),
            @AttributeOverride(name = "en",
                    column = @Column(name = "Name_EN", columnDefinition = DataType.NVARCHAR)),
            @AttributeOverride(name = "zh",
                    column = @Column(name = "Name_ZH", columnDefinition = DataType.NVARCHAR)),
            @AttributeOverride(name = "ja",
                    column = @Column(name = "Name_JA", columnDefinition = DataType.NVARCHAR))})
    private I18N accountName;

    @Comment("계정 설명")
    @Column(name = "Account_DESC", columnDefinition = DataType.NVARCHAR)
    private String accountDesc;

    @Comment("이메일 주소")
    @Column(name = "Email_ADDR", columnDefinition = DataType.NVARCHAR, length = 320)
    private String emailAddr;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "yn",
                    column = @Column(name = "Use_YN", columnDefinition = DataType.NVARCHAR,
                            length = 1, nullable = false)),
            @AttributeOverride(name = "frDt",
                    column = @Column(name = "Use_FR_DT", columnDefinition = DataType.DATE)),
            @AttributeOverride(name = "toDt",
                    column = @Column(name = "Use_TO_DT", columnDefinition = DataType.DATE))})
    private Use use;

    public static interface Setter {

        public void setAccountId(UUID accountId);

        public void setUserId(String userId);

        public void setUserPw(String userPw);

        public void setDisplayNm(String displayNm);

        public void setAccountName(I18N accountName);

        public void setAccountDesc(String accountDesc);

        public void setUse(Use use);

        public void setCreateId(UUID createId);

        public void setCreateName(I18N createName);

        public void setCreateAt(LocalDateTime createAt);

        public void setUpdateId(UUID updateId);

        public void setUpdateName(I18N updateName);

        public void setUpdateAt(LocalDateTime updateAt);
    }

    public static interface Getter {

        public default UUID getAccountId() {
            return null;
        }

        public String getUserId();

        public String getUserPw();

        public String getDisplayNm();

        public I18N getAccountName();

        public String getAccountDesc();

        public Use getUse();
    }
}
