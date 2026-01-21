package kr.co.koreazinc.temp.model.entity.detail.view;

import javax.annotation.concurrent.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Immutable
@Table(name = "v_club_detail")
public class VClubDetail {
	@Id
    @Column(name = "club_id")
    private Integer clubId;

    @Column(name = "club_name")
    private String clubName;

    private String description;
    private String president;

    @Column(name = "established_date")
    private String establishedDate;

    @Column(name = "club_status")
    private String clubStatus;

    @Column(name = "member_cnt")
    private Long memberCnt;

    @Column(name = "request_cnt")
    private Long requestCnt;

    @Column(name = "rule_file_id")
    private Integer ruleFileId;
}
