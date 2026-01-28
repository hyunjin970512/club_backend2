package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "club_apply_fee_rule_detail")
@Getter @Setter
@NoArgsConstructor
public class ClubApplyFeeRuleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    // FK 연관관계 (실제 저장/수정은 여기로)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_id", nullable = false)
    private ClubApplyFeeRuleBas bas;

    // apply_id 숫자값이 필요하면 읽기용으로만
    @Column(name = "apply_id", insertable = false, updatable = false)
    private Long applyId;

    @Column(name = "member_cnt_from", nullable = false)
    private Integer memberCntFrom;

    @Column(name = "member_cnt_to")
    private Integer memberCntTo;

    @Column(name = "pay_amount", nullable = false)
    private Integer payAmount;

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_user", length = 20)
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
