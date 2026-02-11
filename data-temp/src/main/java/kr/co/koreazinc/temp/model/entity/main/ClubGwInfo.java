package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "club_gw_info", schema = "public")
public class ClubGwInfo {

    /** GW 요청 ID (club_create_request.request_id FK) */
    @Id
    @Column(name = "gw_request_id", nullable = false)
    private Long gwRequestId;

    /** 요청 ID */
    @Id
    @Column(name = "request_id", nullable = false)
    private Long requestId;

    /** 동호회 ID */
    @Column(name = "club_id")
    private Long clubId;

    /** 그룹웨어 문서번호 */
    @Column(name = "gw_doc_no", length = 20)
    private String gwDocNo;

    /** 상태 (A 등) */
    @Column(name = "status", length = 20)
    private String status;

    /** 상태 변경일 */
    @Column(name = "status_dt")
    private LocalDate statusDt;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
