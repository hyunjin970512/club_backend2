package kr.co.koreazinc.app.model.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class JoinedClubDto {
	
	@Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
	public static class Get {
	    private Long clubId;
	    private String clubNm;
	    private String clubDesc;
	}
}
