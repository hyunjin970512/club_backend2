package kr.co.koreazinc.app.controller.comm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import kr.co.koreazinc.temp.model.entity.comm.CommonDoc;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common/doc")
public class CommonDocController {
	
	private final CommonDocService commonDocService;
    private final CommonDocRepository commonDocRepository;
    
    @Operation(summary = "로그인 사번")
    @ModelAttribute("loginEmpNo")
    public String getLoginEmpNo(@AuthenticationPrincipal UserPrincipal principal) {
    	if (principal != null) {
    		return principal.getEmpNo();
    	} else {
    		return null;
    	}
    }
    
    /**
     * 파일 업로드 (단건/다건 통합)
     * @param files 클라이언트에서 보낸 파일 리스트
     * @param jobSeCode 업무 구분 코드 (예: 'CB')
     * @param userId 등록자 ID
     * @return 생성된 doc_no 리스트
     */
    @PostMapping("/upload")
    public ResponseEntity<List<Long>> uploadFile(
            @RequestParam("files") List<MultipartFile> files, 
            @RequestParam("jobSeCode") String jobSeCode,
            @RequestParam("empNo") String empNo) throws IOException {
        
    	List<Long> docNos = new ArrayList<>();
    	
    	if (files != null && !files.isEmpty()) {
    		for (MultipartFile file : files) {
    			if (!file.isEmpty()) {
    				// 단건씩 저장 로직 호출
                    Long docNo = commonDocService.saveFile(file, jobSeCode, empNo);
                    docNos.add(docNo);
    			}
    		}
    	}
    	
    	return ResponseEntity.ok(docNos);
    }
    
    /**
     * 파일 다운로드
     * @param docNo 문서 번호
     * @param mode  "view"인 경우 브라우저에서 직접 열기(이미지 등), 그 외에는 다운로드
     */
    @GetMapping("/download/{jobSeCode}/{docNo}")
    public ResponseEntity<byte[]> downloadFile(
    		@PathVariable(name = "jobSeCode") String jobSeCode,
    		@PathVariable(name = "docNo") Long docNo,
    		@RequestParam(name = "mode", defaultValue = "download") String mode) {
    	
    	CommonDoc doc = commonDocRepository.selectById(docNo, jobSeCode);
    	
    	if (doc == null) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	try {
    		// 서비스를 통해 파일 서버로부터 InputStream 획득 후 byte[]로 전환
    		InputStream inputStream = commonDocService.downloadFile(docNo, jobSeCode);
    		byte[] fileData = inputStream.readAllBytes();
    		inputStream.close();
    		
    		// 파일 인코딩
    		String encodedFileName = URLEncoder.encode(doc.getDocFileNm(), StandardCharsets.UTF_8)
    	                .replaceAll("\\+", "%20");
    		
    		// 모드 설정 (미리보기 vs 다운로드)
    		String contentDisposition = "view".equals(mode) ? "inline" : "attachment";
    		
    		return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + encodedFileName + "\"")
                    .contentType(MediaType.parseMediaType(Files.probeContentType(Paths.get(doc.getDocFileNm()))))
                    .contentLength(fileData.length)
                    .body(fileData);
    	} catch (IOException e) {
    		log.error("파일 다운로드 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
    	}
    }
    
    /**
     * 파일 삭제
     * @param docNo 문서 번호
     */
    @PostMapping("/delete/{jobSeCode}/{refId}/{docNo}")
    public ResponseEntity<?> deleteFile(
            @PathVariable("jobSeCode") String jobSeCode,
            @PathVariable("refId") Long refId,
            @PathVariable("docNo") Long docNo,
            @ModelAttribute("loginEmpNo") String empNo) {
    	try {
    		boolean isDeleted = commonDocService.deleteFile(refId, docNo, jobSeCode, empNo);
    		
    		if (isDeleted) {
    			return ResponseEntity.ok(Map.of("success", true, "message", "정상적으로 삭제되었습니다."));
    		} else {
    			return ResponseEntity.status(HttpStatus.NOT_FOUND)
    					.body(Map.of("success", false, "message", "삭제할 파일을 찾을 수 없습니다."));
    		}
    	} catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 오류로 삭제에 실패했습니다."));
        }
    }
}
