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
@Table(name="push_inbox")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushInbox {
	
	public static final String READ_Y = "Y";
	public static final String READ_N = "N";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="emp_no", nullable=false, length=20)
	private String empNo;
	
	@Column(name="message_id", nullable=false)
	private Long messageId;
	
	@Column(name="read_yn", nullable=false, length=1)
	private String readYn; // Y/N
	
	@Column(name="read_at")
	private LocalDateTime readAt;
	
	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;
	
	@PrePersist
	void prePersist() {
		if (createdAt == null) createdAt = LocalDateTime.now();
		if (readYn == null) readYn = READ_N;
	}
	
	public static PushInbox of(String empNo, Long messageId) {
		PushInbox x = new PushInbox();
		x.empNo = empNo;
		x.messageId = messageId;
		return x;
	}
	
	public void markRead() {
		this.readYn = READ_Y;
		this.readAt = LocalDateTime.now();
	}
}
