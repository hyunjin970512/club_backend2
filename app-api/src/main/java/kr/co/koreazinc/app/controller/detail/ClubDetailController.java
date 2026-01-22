package kr.co.koreazinc.app.controller.detail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.github.loki4j.client.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubCommentDto;
import kr.co.koreazinc.app.model.detail.ClubDetailDto;
import kr.co.koreazinc.app.model.detail.ClubFeeInfoDto;
import kr.co.koreazinc.app.service.detail.ClubDetailService;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.model.entity.detail.ClubFeeInfo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/clubs")
public class ClubDetailController {

    private final ClubDetailService clubDetailService;
    
    @Operation(summary = "로그인 사번")
    @ModelAttribute("loginEmpNo")
    public String getLoginEmpNo(Authentication authentication) {
    	if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if(principal instanceof UserDetails) {
            	return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }
		return null;
    }
    
    @Operation(summary = "동호회 기본 정보 조회")
    @GetMapping("/{clubId}/info")
    public ResponseEntity<ClubDetailDto.Get> getCludDetail(@PathVariable("clubId") Integer clubId) {
    	ClubDetailDto.Get result = clubDetailService.getClubDetail(clubId);
    	
    	if(result == null) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "동호회 게시글 목록 조회")
    @GetMapping("/{clubId}/posts")
    public List<Map<String, Object>> getClubPostsList(@PathVariable("clubId") Integer clubId) {
    	return clubDetailService.getClubPostsList(clubId);
    }
    
    @Operation(summary = "동호회 게시글 추천")
    @PostMapping("/posts/{boardId}/recommend")
    public ResponseEntity<Map<String, Object>> recommendPost(@PathVariable("boardId") int boardId) {
    	Map<String, Object> result = new HashMap<>();
    	
    	try {
    		int lastCnt = clubDetailService.updateRecommendPost(boardId);
    		
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
    
    @Operation(summary = "동호회 게시글 조회수 증가")
    @PostMapping("/posts/{boardId}/view")
    public ResponseEntity<Map<String, Object>> viewPost(@PathVariable("boardId") int boardId) {
    	Map<String, Object> result = new HashMap<>();
    	
    	try {
    		int viewCnt = clubDetailService.updateViewCount(boardId);
    		
    		result.put("success", true);
            result.put("lastCnt", viewCnt);
            
            return ResponseEntity.ok(result);
    	} catch (Exception e) {
    		result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    	}
    }
    
    @Operation(summary = "동호회 회비 조회")
    @GetMapping("/{clubId}/fee")
    public ResponseEntity<List<ClubFeeInfoDto.Get>> getClubFeeInfoList(@PathVariable("clubId") Integer clubId) {
        List<ClubFeeInfoDto.Get> list = clubDetailService.getClubFeeInfoList(clubId);
        return ResponseEntity.ok(list);
    }
    
    @Operation(summary = "동호회 회비 수정")
    @PostMapping("/{clubId}/fee/update")
    public ResponseEntity<?> updateClubFeeList(
            @PathVariable("clubId") Integer clubId,
            @RequestBody List<ClubFeeInfoDto.Get> feeList,
            @ModelAttribute("loginEmpNo") String empNo) {
    	try {
    		clubDetailService.updateClubFeeList(clubId, feeList, empNo);
    
            return ResponseEntity.ok()
                    .body(Map.of("success", true, "message", "회비 정보가 수정되었습니다."));
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("success", false, "message", "수정 중 오류 발생: " + e.getMessage()));
    	}
    }
    
    @Operation(summary = "동호회 게시글 등록")
    @PostMapping("/{clubId}/posts/create")
    public Map<String, Object> insertClubPost(
			@PathVariable("clubId") Integer clubId, 
			@RequestPart("data") ClubBoardDto.Get dto, 
			@ModelAttribute("loginEmpNo") String empNo) {
    	Map<String, Object> result = new HashMap<>();
    	
    	dto.setClubId(clubId);
    	dto.setUserEmpNo(empNo);
    	dto.setCreateUser(empNo);
    	
    	boolean isSuccess = clubDetailService.insertClubPost(dto);
    	result.put("success", isSuccess);
    	
    	if (isSuccess) {
            result.put("message", "게시글이 성공적으로 등록되었습니다.");
    	} else {
            result.put("message", "게시글 등록 중 오류가 발생했습니다.");
    	}
    	return result;
    }
    
    @Operation(summary = "동호회 게시글 삭제")
    @PostMapping("/posts/{postId}/delete")
    public ResponseEntity<Map<String, Object>> deletePost(
    		@PathVariable("postId") Integer postId,
    		@RequestBody ClubBoardDto.Delete dto,
    		@ModelAttribute("loginEmpNo") String empNo) {
    	
    	dto.setBoardId(postId);
    	dto.setUserEmpNo(empNo);
    	
    	boolean isSuccess = clubDetailService.deleteClubPost(dto);
    	
    	Map<String, Object> response = new HashMap<>();
    	if (isSuccess) {
    		response.put("success", true);
            response.put("message", "정상적으로 삭제되었습니다.");
    	} else {
    		response.put("success", false);
            response.put("message", "삭제 처리 중 서버 오류가 발생했습니다.");
    	}
    	return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "동호회 게시글 상세 조회")
    @GetMapping("/{id}/posts/{postId}")
    public ResponseEntity<?> getClusPostDetail (
    		@PathVariable("id") Integer clubId,
    		@PathVariable("postId") Integer postId) {
    	
    	ClubBoardDto.Get result = clubDetailService.getClubPostDetail(clubId, postId);
    	
    	if (result == null) {
    		return ResponseEntity.notFound().build();
    	}
    	return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "동호회 댓글 조회")
    @GetMapping("/posts/{boardId}/comments")
    public ResponseEntity<List<ClubCommentDto>> getComments(@PathVariable("boardId") Long boardId) {
    	List<ClubCommentDto> list = clubDetailService.getCommentList(boardId);
    	return ResponseEntity.ok(list);
    }
    
    @Operation(summary = "동호회 게시글 수정")
    @PostMapping("/{clubId}/posts/{boardId}")
    public ResponseEntity<?> updatePost(
    		@PathVariable("clubId") Long clubId,
    		@PathVariable("boardId") Long boardId, 
    		@RequestPart("data") ClubBoardDto.Get dto,
    		@ModelAttribute("loginEmpNo") String empNo) {
    	try {
    		clubDetailService.updatePost(dto, empNo);
    		
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
}