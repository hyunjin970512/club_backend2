package kr.co.koreazinc.app.service.together;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;
import kr.co.koreazinc.temp.repository.together.TogetherBoardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TogetherBoardService {
	
	private final TogetherBoardRepository togetherBoardRepository;
	
	@Transactional
	public Long saveBoard(TogetherBoardDto dto) {
		TogetherBoard entity = TogetherBoard.builder()
				.cludCode(dto.getClubCode())
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
		return saveEntity.getBoardId();
	}
}
