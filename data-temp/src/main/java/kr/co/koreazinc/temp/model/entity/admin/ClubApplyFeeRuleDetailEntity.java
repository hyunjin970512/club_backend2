package kr.co.koreazinc.temp.model.entity.admin;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_apply_fee_rule_detail")
@Getter @Setter
@NoArgsConstructor
public class ClubApplyFeeRuleDetailEntity {

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
