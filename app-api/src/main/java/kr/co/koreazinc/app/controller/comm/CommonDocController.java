package kr.co.koreazinc.app.controller.comm;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;

import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.temp.model.entity.comm.CommonDoc;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common/doc")
public class CommonDocController {
	
	private final CommonDocService commonDocService;
    private final CommonDocRepository commonDocRepository;
    
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
     */
    @GetMapping("/download/{docNo}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long docNo) {
    	CommonDoc doc = commonDocRepository.selectQuery()
                .eqDocNo(docNo)
                .fetchOne();
    	
    	// 파일명 인코딩 (공백 처리 포함)
        String encodedFileName = URLEncoder.encode(doc.getDocFileNm(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(doc.getDocFileData().length)
                .body(doc.getDocFileData());
    }
}
