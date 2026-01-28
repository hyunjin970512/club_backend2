package kr.co.koreazinc.app.model.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubAuthDto {
	/** 사용자 역할 코드 (00: 방장, 10: 일반멤버, 미가입자) */
	private String userRoleCd;
	
	/** 활동 상태 코드 (10: 가입, 20: 탈퇴) */
	private String status;
	
	private String empNo;
	
	/** 가입 신청 상태 코드 (10: 신청, 20: 승인, 30: 반려, 40: 취소) */
	private String joinStatus;
}