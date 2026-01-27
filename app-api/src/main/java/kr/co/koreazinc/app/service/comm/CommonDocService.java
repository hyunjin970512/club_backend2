package kr.co.koreazinc.app.service.comm;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.spring.model.FileInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.temp.model.entity.comm.CommonDoc;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import kr.co.koreazinc.temp.repository.comm.CommonMappingDocRepository;
import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.spring.utility.FileUtils;

@Service
@RequiredArgsConstructor
public class CommonDocService {
	
	private final CommonDocRepository commonDocRepository;
	private final CommonMappingDocRepository commonMappingDocRepository;
	private final OAuth2Property property;

	// 파일 업로드
	@Transactional
	public Long saveFile(MultipartFile files, String jobSeCode, String empNo) throws IOException {
		FileInfo file = FileInfo.builder()
				.file(files.getInputStream())
				.system("app")
				.corporation("global")
				.path("/")
				.name(files.getOriginalFilename())
				.size(files.getSize())
				.build();

		FileInfo result = FileUtils.remoteUpload(property.getCredential("file"), file);
		if (result == null || result.getPath() == null) {
		    throw new IOException("파일 서버 업로드에 실패하였습니다. (응답 없음)");
		}

		CommonDoc entity = CommonDoc.builder().jobSeCode(jobSeCode).docFileNm(files.getOriginalFilename())
				.filePath(result.getPath()).saveFileNm(result.getName()).deleteYn("N").createUser(empNo)
				.createDate(LocalDateTime.now()).updateUser(empNo).updateDate(LocalDateTime.now()).build();

		CommonDoc saved = commonDocRepository.insert(entity);
		return saved.getDocNo();
	}

	// 파일 ID와 맵핑 작업
	public void saveMapping(Long refId, Long docNo, String empNo) {
		CommonMappingDoc entity = CommonMappingDoc.builder().refId(refId).docNo(docNo).deleteYn("N").createUser(empNo)
				.createDate(LocalDateTime.now()).updateUser(empNo).updateDate(LocalDateTime.now()).build();

		commonMappingDocRepository.insert(entity);
	}
	
	
	// 파일 다운로드
	public InputStream downloadFile(Long docNo, String jobSeCode) throws IOException {
		CommonDoc doc = commonDocRepository.selectById(docNo, jobSeCode);
		
		if(doc == null) {
			throw new IOException("파일이 존재하지 않습니다. ID: " + docNo);
		}
		
		if (doc.getFilePath() == null) {
	        throw new IOException("파일 서버 경로 정보가 누락되었습니다.");
	    }
		
		FileInfo file = FileInfo.builder()
				.system("app")
				.corporation("global")
				.path(doc.getFilePath())
				.name(doc.getSaveFileNm())
				.build();
		
		return FileUtils.remoteDownload(property.getCredential("file"), file);
	}
	
	// 파일 삭제
	@Transactional
	public boolean deleteFile(Long docNo, String jobSeCode, String empNo) {
		// 1. CO_COMMON_DOC 업데이트
		commonDocRepository.deleteFile(docNo, jobSeCode, empNo);
		// 2. CO_COMMON_MAPPING_DOC 업데이트
        commonMappingDocRepository.deleteMapFile(docNo, empNo);
        return true;
	}
}
