package kr.co.koreazinc.temp.model.entity.push;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "push_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="event_type", nullable=false, length=50)
	private String eventType;
	
	@Column(name="title", nullable=false, length=200)
	private String title;
	
	@Column(name="body", nullable=false, length=1000)
	private String body;
	
	@Column(name="link_url", length=500)
	private String linkUrl;
	
	@Column(name="payload_json", columnDefinition = "text")
	private String payloadJson;
	
	@Column(name="created_by", length=20)
	private String createdBy; // emp_no
	
	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;
	
	@PrePersist
	void prePersist() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}
	
	public static PushMessage of(
									String eventType,
									String title,
									String body,
									String linkUrl,
									String payloadJson,
									String createdBy
								) {
		PushMessage m = new PushMessage();
		m.eventType = eventType;
		m.title = title;
		m.body = body;
		m.linkUrl = linkUrl;
		m.payloadJson = payloadJson;
		m.createdBy = createdBy;
		return m;
	}
}
