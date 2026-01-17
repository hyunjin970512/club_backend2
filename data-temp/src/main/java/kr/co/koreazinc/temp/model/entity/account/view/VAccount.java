package kr.co.koreazinc.temp.model.entity.account.view;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import kr.co.koreazinc.data.support.DataType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Immutable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "V_Account")
@Entity
public class VAccount {

    @Id
    @Comment("고유 아이디")
    @JdbcTypeCode(Types.NVARCHAR)
    @Column(name = "Summary_ID", columnDefinition = DataType.NVARCHAR, length = 36)
    private UUID id;

    @Comment("매핑 아이디")
    @JdbcTypeCode(Types.NVARCHAR)
    @Column(name = "Mapping_ID", columnDefinition = DataType.NVARCHAR, length = 36)
    private UUID mappingId;

    @Comment("계정 아이디")
    @JdbcTypeCode(Types.NVARCHAR)
    @Column(name = "Account_ID", columnDefinition = DataType.NVARCHAR, length = 36)
    private UUID accountId;

    @Comment("사용자 아이디")
    @Column(name = "User_ID", columnDefinition = DataType.NVARCHAR, length = 100)
    private String userId;

    @Comment("그룹 아이디")
    @JdbcTypeCode(Types.NVARCHAR)
    @Column(name = "Group_ID", columnDefinition = DataType.NVARCHAR, length = 36)
    private UUID groupId;

    @Comment("그룹 코드")
    @Column(name = "Group_CD", columnDefinition = DataType.NVARCHAR, length = 100)
    private String groupCd;

    public static interface Setter {

        public void setId(UUID id);

        public void setMappingId(UUID mappingId);

        public void setAccountId(UUID accountId);

        public void setUserId(String userId);

        public void setGroupId(UUID groupId);

        public void setGroupCd(String groupCd);

        public void setCreateId(UUID createId);

        public void setCreateName(I18N createName);

        public void setCreateAt(LocalDateTime createAt);

        public void setUpdateId(UUID updateId);

        public void setUpdateName(I18N updateName);

        public void setUpdateAt(LocalDateTime updateAt);
    }
}
