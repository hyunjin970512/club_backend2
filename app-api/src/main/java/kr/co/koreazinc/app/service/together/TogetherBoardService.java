package kr.co.koreazinc.app.service.together;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.app.model.together.TogetherCommentDto;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;
import kr.co.koreazinc.temp.model.entity.together.TogetherComment;
import kr.co.koreazinc.temp.repository.together.TogetherBoardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TogetherBoardService {
	
	private final TogetherBoardRepository togetherBoardRepository;
	private final CommonDocService commonDocService;
	
	/**
     * 투게더 게시글 작성
     */
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
	
	/**
     * 투게더 게시물 추천하기
     */
	@Transactional
	public int updateRecommendPost(Long boardId) {
		return togetherBoardRepository.updateRecommendPost(boardId);
	}
	
	/**
    * 투게더 게시글 조회수 증가
    */
	@Transactional
	public int updateViewCount(Long boardId) {
		return togetherBoardRepository.updateViewCount(boardId);
	}
	
	/**
     * 투게더 게시글 상세 조회
     */
	@Transactional
	public TogetherBoardDto getTogetherPostDetail(Long boardId) {
		TogetherBoardDto detail = togetherBoardRepository.selectTogetherPostDetail(TogetherBoardDto.class, boardId);
		
		if (detail != null) {
			List<TogetherBoardDto.FileDto> files = togetherBoardRepository.selectPostFiles(TogetherBoardDto.FileDto.class, boardId);
			files.forEach(file -> {
				// 이미지를 보여줄 경로 (mode = view)
				file.setDisplayUrl("/api/common/doc/download/TO/" + file.getDocNo() + "?mode=view");
				// 파일을 다운로드할 경로 (기본값 download)
		        file.setDownloadUrl("/api/common/doc/download/TO/" + file.getDocNo());
			});
			detail.setFiles(files);
		}
		
		return detail;
	}
	
	/**
     * 투게더 댓글 조회
     */
	public List<TogetherCommentDto> getCommentList(Long boardId) {
		return togetherBoardRepository.selectCommentList(TogetherCommentDto.class, boardId);
	}
	
}
