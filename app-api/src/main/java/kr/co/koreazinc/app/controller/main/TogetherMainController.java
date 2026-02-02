package kr.co.koreazinc.app.controller.main;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.koreazinc.app.model.main.AreaDto;
import kr.co.koreazinc.app.model.main.TogetherBoardListDto;
import kr.co.koreazinc.app.servie.main.TogetherMainService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/together")
public class TogetherMainController {
	
	private final TogetherMainService togetherMainService;
	
	@GetMapping("/areas")
	@Operation(summary = "사업장 조회")
    public List<AreaDto> areas() {
        return togetherMainService.getAreas();
    }
	
	@GetMapping("/type")
	@Operation(summary = "팀투게더 반투게더 조회")
	public List<AreaDto> type() {
		return togetherMainService.getType();
	}
	
	@GetMapping("/posts")
	@Operation(summary = "투게더 게시글 목록 조회(검색/필터)")
	public List<TogetherBoardListDto> posts(
		@RequestParam(name = "siteType", required = false) String siteType, // ALL|HQ|OS|ET
		@RequestParam(name = "type", required = false) String type,         // ALL|10|20
		@RequestParam(name = "q", required = false) String q                // 검색어
	) {
		return togetherMainService.getBoardList(siteType, type, q);
	}

}
