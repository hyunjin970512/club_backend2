package kr.co.koreazinc.app.service.comm;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.temp.model.entity.comm.CommonDoc;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import kr.co.koreazinc.temp.repository.comm.CommonMappingDocRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonDocService {
	
	private final CommonDocRepository commonDocRepository;
	private final CommonMappingDocRepository commonMappingDocRepository;
	
	// 파일 데이터를 DB에 저장 (byte[] 변환)
	@Transactional
	public Long saveFile(MultipartFile file, String jobSeCode, String empNo) throws IOException {
		CommonDoc entity = CommonDoc.builder()
				.jobSeCode(jobSeCode)
				.docFileNm(file.getOriginalFilename())
				.docFileData(file.getBytes()) // MultipartFile을 byte 배열로 추출
				.deleteYn("N")
				.createUser(empNo)
				.createDate(LocalDateTime.now())
				.updateUser(empNo)
				.updateDate(LocalDateTime.now())
				.build();
		
		CommonDoc saved = commonDocRepository.insert(entity);
        return saved.getDocNo();
	}
	
	// 파일 ID와 맵핑 작업
	public void saveMapping(Long refId, Long docNo, String empNo) {
		CommonMappingDoc entity = CommonMappingDoc.builder()
				.refId(refId)
				.docNo(docNo)
				.deleteYn("N")
				.createUser(empNo)
				.createDate(LocalDateTime.now())
				.updateUser(empNo)
				.updateDate(LocalDateTime.now())
				.build();
		
		commonMappingDocRepository.insert(entity);
	}
}
