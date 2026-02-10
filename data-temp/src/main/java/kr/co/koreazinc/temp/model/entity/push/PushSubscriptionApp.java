package kr.co.koreazinc.temp.model.entity.push;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "push_subscription_app", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_no", nullable = false, length = 20)
    private String empNo;

    @Column(name = "token", nullable = false, length = 500, unique = true)
    private String token;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "active_yn", nullable = false, length = 1)
    private String activeYn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
