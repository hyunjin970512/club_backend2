package kr.co.koreazinc.temp.model.entity.main;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "apply_id", nullable = false)
    private Long applyId;

    @Column(name = "member_cnt_from", nullable = false)
    private Integer memberCntFrom;

    @Column(name = "member_cnt_to")
    private Integer memberCntTo;

    @Column(name = "pay_amount", nullable = false)
    private Integer payAmount;
}
