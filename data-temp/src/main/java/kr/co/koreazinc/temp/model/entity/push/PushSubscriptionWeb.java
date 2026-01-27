package kr.co.koreazinc.temp.model.entity.push;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "push_subscription_web",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_push_subscription_web_endpoint",
			columnNames = {"endpoint"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushSubscriptionWeb {

	public static final String Y = "Y";
	public static final String N = "N";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "emp_no", nullable = false, length = 20)
	private String empNo;
	
	@Column(nullable = false, length = 1000) // ✅ endpoint 길이 여유
	private String endpoint;
	
	@Column(nullable = false, length = 512)
	private String p256dh;
	
	@Column(nullable = false, length = 200)
	private String auth;
	
	@Column(name = "user_agent", length = 500)
	private String userAgent;
	
	@Column(name = "active_yn", nullable = false, length = 1)
	private String activeYn;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) createdAt = now;
		if (updatedAt == null) updatedAt = now;
		if (activeYn == null) activeYn = Y;
	}
	
	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
	
	/**
	 * 구독 활성화/갱신
	 */
	public void activate(String empNo, String p256dh, String auth, String userAgent) {
		this.empNo = empNo;
		this.p256dh = p256dh;
		this.auth = auth;
		this.userAgent = userAgent;
		this.activeYn = Y;
	}
	
	public void deactivate() {
		this.activeYn = N;
	}
	
	public static PushSubscriptionWeb of(
											String empNo,
											String endpoint,
											String p256dh,
											String auth,
											String userAgent
	) {
		PushSubscriptionWeb s = new PushSubscriptionWeb();
		s.empNo = empNo;
		s.endpoint = endpoint;
		s.p256dh = p256dh;
		s.auth = auth;
		s.userAgent = userAgent;
		s.activeYn = Y;
		return s;
	}
}
