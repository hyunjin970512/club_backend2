package kr.co.koreazinc.app.controller.chk;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.chk.ChkService;
import kr.co.koreazinc.app.service.security.dto.ApiResponse;
import kr.co.koreazinc.app.service.security.dto.Meresponse;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChkContoller {
	
	private final ChkService chkService;
	private final CurrentUserService currentUserService;

	@GetMapping("/api/me")
	public ApiResponse<Meresponse> me(@AuthenticationPrincipal UserPrincipal principal) {
		if (principal == null) {
	    return ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다.");
	  }
		
	  // 가입된 동호회 ID 리스트 가져오기
	  List<Long> joinedClubIds = currentUserService.getJoinedClubIds();
	  // 가입 요청중인 동호회 ID 리스트 가져오기
	  List<Long> joinRequestClubIds = currentUserService.getJoinRequestClubIds();
	  // 기본 유저 정보 가져오기
	  Meresponse me = chkService.me(principal.getEmpNo());

	  return ApiResponse.ok(me.toBuilder()
			  				.joinedClubIds(joinedClubIds)
			  				.joinRequestClubIds(joinRequestClubIds)
			  				.build());
	}
	
}
