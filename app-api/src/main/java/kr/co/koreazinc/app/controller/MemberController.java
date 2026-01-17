package kr.co.koreazinc.app.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.koreazinc.temp.model.entity.account.Meresponse;
import kr.co.koreazinc.temp.model.entity.account.ApiResponse;
import kr.co.koreazinc.app.model.security.UserPrincipal;
import kr.co.koreazinc.app.service.account.MemberService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;
	
	@GetMapping("/me")
	public ApiResponse<Meresponse> me(@AuthenticationPrincipal UserPrincipal principal) {
	  if (principal == null) {
	    return ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다.");
	  }
	  String empNo = principal.getEmpNo();
	  return ApiResponse.ok(memberService.me(empNo));
	}

	
}
