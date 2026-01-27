package kr.co.koreazinc.app.model.form;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class ClubJoinRequestDto {
	
	@Getter @Setter
	public static class JoinCheckResponse {
	    private boolean joined;      // status=20
	    private boolean requested;   // club_join_request 존재(신청중)
	    private Long requestId;      // 신청중이면 requestId
	    private String message;      // 안내문구
	}

    @Getter @Setter
    public static class Create {
        private Long clubId;
        private String applyReason;
    }

    @Getter @Setter
    public static class Update {
        private Long clubId;
        private String applyReason;
    }

    @Getter @Setter
    public static class Response {
        private Long requestId;
        private Long clubId;
        private String applyReason;
        private String status;
        private String requestUser;
        private String requestDate;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClubSimpleResponse {
        private Long clubId;
        private String clubNm;
    }
    
    @Getter @Setter
    public static class ClubPushTarget {
      private Long clubId;
      private String clubName;
      private String clubMasterId; // 동호회장 empNo
    }

}
