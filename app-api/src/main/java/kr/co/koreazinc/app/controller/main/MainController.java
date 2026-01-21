package kr.co.koreazinc.app.controller.main;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.koreazinc.app.model.main.ApplyFeeRuleDetailDto;
import kr.co.koreazinc.app.model.main.AreaDto;
import kr.co.koreazinc.app.model.main.ClubListDto;
import kr.co.koreazinc.app.model.main.JoinedClubDto;
import kr.co.koreazinc.app.model.main.MenuDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.servie.main.MainService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
@Tag(name = "MainController", description = "main 화면 사용 데이터 조회")
public class MainController {

    private final MainService mainService;
    
	private final CurrentUserService currentUser;
	
	@GetMapping("/menus")
    @Operation(summary = "메뉴 조회")
    public ResponseEntity<List<MenuDto.Get>> menus() {
        return ResponseEntity.ok(mainService.getMenus(currentUser.empNoOrThrow()));
    }
	
	@GetMapping("/current")
	@Operation(summary = "동호회 지원금 지급 규정")
	public List<ApplyFeeRuleDetailDto> current() {
	    return mainService.getCurrentRules();
	}
	
	@GetMapping("/chkClubCnt")
	@Operation(summary = "가입한 동호회 개수 조회")
	public int chkClubCnt() {
	    return mainService.chkClubCnt(currentUser.empNoOrThrow());
	}
	
	@GetMapping("/joined")
	@Operation(summary = "가입한 동호회 목록 조회")
	public ResponseEntity<List<JoinedClubDto.Get>> joined() {
	    return ResponseEntity.ok(mainService.getJoinedClubs(currentUser.empNoOrThrow()));
	}
	
	@GetMapping("/areas")
	@Operation(summary = "동호회 목록 필터(칩) 조회")
    public List<AreaDto> areas() {
        return mainService.getAreas();
    }
	
	/**
     * 동호회 목록
     */
    @GetMapping("/clubs")
    @Operation(summary = "동호회 목록 조회 (ALL, KZ, OS)")
    public List<ClubListDto> clubs(@RequestParam(value = "area", required = false) String area) {
        return mainService.getClubs(area);
    }

}
