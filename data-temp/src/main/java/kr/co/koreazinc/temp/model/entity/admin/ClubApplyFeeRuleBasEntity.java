package kr.co.koreazinc.temp.model.entity.admin;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_apply_fee_rule_bas")
@Getter @Setter
@NoArgsConstructor
public class ClubApplyFeeRuleBasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long applyId;

    @Column(name = "apply_start_dt")
    private LocalDate applyStartDt;

    @Column(name = "apply_end_dt")
    private LocalDate applyEndDt;

    @Column(name = "use_yn", columnDefinition = "bpchar(1)")
    private String useYn; // "Y" / "N"
}
