package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_create_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubCreateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "club_id")
    private Long clubId;

    @Column(name = "club_nm", length = 100, nullable = false)
    private String clubNm;

    @Column(name = "club_desc")
    private String clubDesc;

    @Column(name = "club_master_key", length = 20, nullable = false)
    private String clubMasterKey;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "rule_file_id")
    private Long ruleFileId;

    @Column(name = "member_file_id")
    private Long memberFileId;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
