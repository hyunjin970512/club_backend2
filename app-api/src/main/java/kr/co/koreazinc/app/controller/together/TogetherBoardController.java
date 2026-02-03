package kr.co.koreazinc.app.controller.together;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.loki4j.client.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubCommentDto;
import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.app.model.together.TogetherCommentDto;
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
	
	@Operation(summary = "투게더 게시글 수정")
	@PostMapping("/posts/{boardId}")
	public ResponseEntity<?> updatePost(
			@PathVariable("boardId") Long boardId, 
			@RequestPart("data") TogetherBoardDto postDto,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@AuthenticationPrincipal UserPrincipal principal) {
		try {
			togetherBoardService.updatePost(postDto, files, principal.getEmpNo());
			
			return ResponseEntity.ok(Map.of(
    	            "success", true,
    	            "message", "게시글이 성공적으로 수정되었습니다."
    	        ));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("success", false, "message", "수정 중 오류 발생: " + e.getMessage()));
		}
	}
	
	@Operation(summary = "투게더 게시글 삭제")
	@PostMapping("/posts/{boardId}/delete")
	public ResponseEntity<Map<String, Object>> deletePost(
			@PathVariable("boardId") Long boardId, 
			@AuthenticationPrincipal UserPrincipal principal) {
		try {
			togetherBoardService.deletePost(boardId, principal.getEmpNo());
			
			return ResponseEntity.ok(Map.of(
		            "success", true,
		            "message", "게시글 삭제되었습니다."
		        ));
		} catch (Exception e) {
			e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	                .body(Map.of("success", false, "message", "삭제 중 오류가 발생했습니다."));
		}
	}
	
	@Operation(summary = "투게더 게시글 조회수 증가")
    @PostMapping("/posts/{boardId}/view")
	 public ResponseEntity<Map<String, Object>> viewPost(@PathVariable("boardId") Long boardId) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			int viewCnt = togetherBoardService.updateViewCount(boardId);
			
			result.put("success", true);
            result.put("lastCnt", viewCnt);
            
            return ResponseEntity.ok(result);
		} catch (Exception e) {
    		result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    	}
	}
	
	@Operation(summary = "동호회 게시글 추천")
    @PostMapping("/posts/{boardId}/recommend")
	public ResponseEntity<Map<String, Object>> recommendPost(@PathVariable("boardId") Long boardId) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			int lastCnt = togetherBoardService.updateRecommendPost(boardId);
			
			result.put("success", true);
            result.put("lastCnt", lastCnt);
            result.put("message", "추천이 완료되었습니다.");
            
            return ResponseEntity.ok(result);
		} catch (Exception e) {
    		result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    	}
	}
	
	@Operation(summary = "투게더 게시글 상세 조회")
    @GetMapping("/posts/{boardId}")
	public Map<String, Object> getTogetherPostDetail(@PathVariable("boardId") Long boardId) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			TogetherBoardDto data = togetherBoardService.getTogetherPostDetail(boardId);
			
			if (data != null) {
				result.put("success", true);
				result.put("data", data);
			} else {
				result.put("success", false);
	            result.put("message", "게시글을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			log.error("조회 중 오류 발생: ", e);
	        result.put("success", false);
	        result.put("message", "데이터를 가져오는 중 오류가 발생했습니다.");
		}
		
		return result;
	}
	
	@Operation(summary = "투게더 댓글 조회")
    @GetMapping("/posts/{boardId}/comments")
    public ResponseEntity<List<TogetherCommentDto>> getComments(@PathVariable("boardId") Long boardId) {
    	List<TogetherCommentDto> list = togetherBoardService.getCommentList(boardId);
    	return ResponseEntity.ok(list);
    }
	
	@Operation(summary = "투게더 댓글 저장")
	@PostMapping("/posts/{boardId}/comment/save")
	public ResponseEntity<?> addComment(@PathVariable("boardId") Long boardId, @RequestBody TogetherCommentDto dto, @AuthenticationPrincipal UserPrincipal principal) {
		dto.setBoardId(boardId);
		
		if (principal != null) {
			dto.setCreateUser(principal.getEmpNo());
			dto.setUpdateUser(principal.getEmpNo());
		}
		
		togetherBoardService.saveComment(dto);
		return ResponseEntity.ok().body(Map.of("success", true));
	}
	
	@Operation(summary = "투게더 댓글 수정")
    @PostMapping("/posts/{boardId}/comment/{commentId}/update")
    public ResponseEntity<?> updateComment(
    		@PathVariable("boardId") Long boardId, 
    		@PathVariable("commentId") Long commentId,
    		@RequestBody TogetherCommentDto dto,
    		@AuthenticationPrincipal UserPrincipal principal) {
		dto.setCommentId(commentId);
		dto.setBoardId(boardId);
		dto.setUpdateUser(principal.getEmpNo());
		
		togetherBoardService.updateComment(boardId, dto);
		
		return ResponseEntity.ok(Map.of("success", true));
	}
	
	@Operation(summary = "동호회 댓글 삭제")
    @PostMapping("/posts/{boardId}/comment/{commentId}/delete")
    public ResponseEntity<?> deleteComment(
    		@PathVariable("boardId") Long boardId, 
    		@PathVariable("commentId") Long commentId,
    		@AuthenticationPrincipal UserPrincipal principal) {
		
		TogetherCommentDto dto = new TogetherCommentDto();
		dto.setCommentId(commentId);
		dto.setUpdateUser(principal.getEmpNo());
		
		togetherBoardService.deleteComment(boardId, dto);
		
		return ResponseEntity.ok(Map.of("success", true)); 
	}
}
