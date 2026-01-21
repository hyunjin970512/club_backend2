package kr.co.koreazinc.app.controller.chk;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.koreazinc.app.service.chk.ChkService;
import kr.co.koreazinc.app.service.security.dto.ApiResponse;
import kr.co.koreazinc.app.service.security.dto.Meresponse;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChkContoller {
	
	private final ChkService chkService;

	@GetMapping("/api/me")
	public ApiResponse<Meresponse> me(@AuthenticationPrincipal UserPrincipal principal) {
		if (principal == null) {
	    return ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다.");
	  }
	  String empNo = principal.getEmpNo();
	  return ApiResponse.ok(chkService.me(empNo));
	}
	
}
