package kr.co.koreazinc.app.model.push;

// 푸시 알림 타입 정리
public enum PushType {
	CLUB_JOIN_REQUEST,      // 가입 신청 → 동호회장
	CLUB_JOIN_APPROVED,     // 승인 → 신청자
	CLUB_JOIN_REJECTED,     // 거절 → 신청자
	//동호회
	POST_CREATED,           // 게시글 등록 → 동호회원들
	COMMENT_CREATED,        // 댓글 → 게시글 작성자
	REPLY_CREATED,           // 대댓글 → 댓글 작성자
	//투게더
	POST_CREATED_TO,           // 게시글 등록 → 모든사용자
	COMMENT_CREATED_TO,        // 댓글 → 게시글 작성자
	REPLY_CREATED_TO           // 대댓글 → 댓글 작성자
}
