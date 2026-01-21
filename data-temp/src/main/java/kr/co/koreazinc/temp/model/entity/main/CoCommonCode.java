package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "co_common_code")
public class CoCommonCode {

    @EmbeddedId
    private CoCommonCodeId id;

    @Column(name = "code_nm", length = 50, nullable = false)
    private String codeNm;

    @Column(name = "code_reference", length = 100)
    private String codeReference;

    @Column(name = "use_at", nullable = false)
    private String useAt;     // 'Y' / 'N' (bpchar(1))

    @Column(name = "delete_at", nullable = false)
    private String deleteAt;  // 'N' / 'Y'

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
