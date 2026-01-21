package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "club_join_request")
public class ClubJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Column(name = "request_user", nullable = false, length = 20)
    private String requestUser;

    @Column(name = "apply_reason")
    private String applyReason;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
