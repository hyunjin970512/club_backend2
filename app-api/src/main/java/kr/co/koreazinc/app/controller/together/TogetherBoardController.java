package kr.co.koreazinc.app.controller.together;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import kr.co.koreazinc.app.service.together.TogetherBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/together")
public class TogetherBoardController {
	
	private final TogetherBoardService togetherBoardService;
	
	@Operation(summary = "투게더 게시글 등록")
    @PostMapping("/posts/create")
	public Map<String, Object> insertTogetherPost(
			@RequestPart("data") TogetherBoardDto postDto,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@AuthenticationPrincipal UserPrincipal principal) {
		Map<String, Object> result = new HashMap<>();
		 
		if (principal != null) {
			postDto.setCreateUser(principal.getEmpNo());
			postDto.setUpdateUser(principal.getEmpNo());
		}
		
		try {
			Long boardId = togetherBoardService.insertTogetherPost(postDto, files);
			
			result.put("success", true);
			result.put("boardId", boardId);
		    result.put("message", "등록되었습니다.");
		} catch (Exception e) {
			log.error("게시글 등록 중 오류 발생: ", e);
            result.put("success", false);
            result.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
		}
		return result;
	}
    
}
