package kr.co.koreazinc.app.service.together;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;
import kr.co.koreazinc.temp.repository.together.TogetherBoardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TogetherBoardService {
	
	private final TogetherBoardRepository togetherBoardRepository;
	private final CommonDocService commonDocService;
	
	@Transactional
	public Long insertTogetherPost(TogetherBoardDto dto, List<MultipartFile> files) throws IOException {
		// Entity 변환 및 저장
		TogetherBoard entity = TogetherBoard.builder()
				.clubCode(dto.getClubCode())
				.togetherCode(dto.getTogetherCode())
				.title(dto.getTitle())
				.content(dto.getContent())
				.noticeDt(dto.getNoticeDt())
				.viewCnt(0)
				.recomendCnt(0)
				.deleteYn("N")
				.createUser(dto.getCreateUser())
				.createDate(LocalDateTime.now())
				.updateUser(dto.getUpdateUser())
				.updateDate(LocalDateTime.now())
				.build();
		
		TogetherBoard saveEntity = togetherBoardRepository.save(entity);
		Long boardId = saveEntity.getBoardId();
		String empNo = dto.getCreateUser();
		
		// 첨부파일 처리
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				if(!file.isEmpty()) {
					Long docNo = commonDocService.saveFile(file, "TO", empNo);
					
					if (docNo != null) {
						commonDocService.saveMapping(saveEntity.getBoardId(), docNo, empNo);
					}
				}
			}
		}
		return boardId;
	}
}
