package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "club_info")
@Getter
@NoArgsConstructor
public class ClubInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;

    @Column(name = "club_nm", nullable = false, length = 100)
    private String clubNm;

    @Column(name = "club_master_id", nullable = false, length = 20)
    private String clubMasterId;

    @Column(name = "establish_dt")
    private LocalDate establishDt;

    @Column(name = "create_request_id", length = 20)
    private String createRequestId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "club_type", length = 20)
    private String clubType;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}

