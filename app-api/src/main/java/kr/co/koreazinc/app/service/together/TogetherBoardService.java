package kr.co.koreazinc.app.service.together;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.main.ClubMemberDto;
import kr.co.koreazinc.app.model.push.PushType;
import kr.co.koreazinc.app.model.together.TogetherBoardDto;
import kr.co.koreazinc.app.model.together.TogetherCommentDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.app.service.push.PushFacade;
import kr.co.koreazinc.temp.model.entity.account.CoEmplBas;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;
import kr.co.koreazinc.temp.model.entity.together.TogetherComment;
import kr.co.koreazinc.temp.repository.comm.CommonMappingDocRepository;
import kr.co.koreazinc.temp.repository.main.GetCoEmpListForTogether;
import kr.co.koreazinc.temp.repository.main.GetTogetherCommentReceiverRepository;
import kr.co.koreazinc.temp.repository.together.TogetherBoardRepository;
import kr.co.koreazinc.temp.repository.together.TogetherCommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TogetherBoardService {
	
	private final TogetherBoardRepository togetherBoardRepository;
	private final TogetherCommentRepository togetherCommentRepository;
	private final CommonDocService commonDocService;
	private final CommonMappingDocRepository commonMappingDocRepository;
	
	private final GetCoEmpListForTogether getTogetherDataRepository;
	private final GetTogetherCommentReceiverRepository getTogetherCommentReceiverRepository;
	
	//push
	private final PushFacade pushFacade;
	
	private final CurrentUserService currentUser;
	
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
		
		String typeCd = "10".equals(dto.getTogetherCode()) ? "팀" : "20".equals(dto.getTogetherCode()) ? "반" : "";
		
		if(boardId != null) {
			
			List<String> empNos = getTogetherDataRepository.selectEmpNoListForTogether();
			
			Map<String, Object> data = Map.of(
					"typeCd", typeCd,
					"authorNm", currentUser.nameKoreanOrThrow(),
					"postTitle", dto.getTitle(),
					"boardId", boardId
				);
			
			for (String sendEmpNo : empNos) {

				/*if (empNo != null && empNo.equals(sendEmpNo)) continue;*/
				
				pushFacade.send(
						PushType.POST_CREATED_TO,
						List.of(sendEmpNo),
						data,
						empNo // createdByEmpNo
						);
			}
		}
		
		return boardId;
	}
	
	/**
     * 투게더 게시글 수정
     */
	@Transactional
	public void updatePost(TogetherBoardDto dto, List<MultipartFile> files, String empNo) throws Exception {
		TogetherBoard post = togetherBoardRepository.findOne(dto.getBoardId());
		long boardId = dto.getBoardId();
		String jobSeCode = "TO";
		
		if(!post.getCreateUser().equals(empNo)) {
			throw new SecurityException("본인이 작성한 글만 수정할 수 있습니다.");
		}
		
		// 게시글 정보 업데이트
		post.update(dto.getClubCode(), dto.getTogetherCode(), dto.getTitle(), dto.getContent(), dto.getNoticeDt(), empNo);
		
		// 현재 맵핑된 파일 리스트 조회
		List<CommonMappingDoc> currentMappings = commonMappingDocRepository.findByRefId(boardId);
		// 유지할 파일 ID 목록
		List<Long> keepFileIds = dto.getExistFileId() != null ? dto.getExistFileId() : new ArrayList<>();
		
		// 삭제처리
		for (CommonMappingDoc mapping : currentMappings) {
			if (!keepFileIds.contains(mapping.getDocNo())) {
				commonDocService.deleteFile(boardId, mapping.getDocNo(), jobSeCode, empNo);
			}
		}
		
		// 신규 추가
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					Long newDocNo = commonDocService.saveFile(file, jobSeCode, empNo);
					commonDocService.saveMapping(boardId, newDocNo, empNo);
				}
			}
		}
	}
	
	/**
     * 투게더 게시글 삭제
     */
	@Transactional
	public void deletePost(Long boardId, String empNo) throws Exception {
		TogetherBoard post = togetherBoardRepository.findOne(boardId);
		
		if (post == null) {
			throw new RuntimeException("해당 게시글을 찾을 수 없습니다.");
		}
		
		if (!post.getCreateUser().equals(empNo)) {
	        throw new SecurityException("본인이 작성한 글만 삭제할 수 있습니다.");
	    }
		// 게시글 삭제
		post.delete(empNo);
		// 해당 게시글의 모든 댓글 삭제
		togetherCommentRepository.deleteCommentsByBoardId(boardId, empNo);
		// 첨부파일 삭제
		List<CommonMappingDoc> mapping = commonMappingDocRepository.findByRefId(boardId);
		
		if (mapping != null) {
			for (CommonMappingDoc map : mapping) {
				commonDocService.deleteFile(boardId, map.getDocNo(), "TO", empNo);
			}
		}
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
		return togetherCommentRepository.selectCommentList(TogetherCommentDto.class, boardId);
	}
	
	/**
     * 투게더 댓글 작성
     */
	@Transactional
	public void saveComment(TogetherCommentDto dto) {
		// 게시글 객체 조회
		TogetherBoard board = togetherBoardRepository.findOne(dto.getBoardId());
		
		if (board == null) {
			throw new RuntimeException("해당 게시글을 찾을 수 없습니다. ID: " + dto.getBoardId());
		}
		
		TogetherComment entity = TogetherComment.builder()
				.boardId(board)
				.parentCommentId(dto.getParentCommentId())
				.content(dto.getContent())
				.recomendCnt(0)
				.deleteYn("N")
				.createUser(dto.getCreateUser())
				.createDate(LocalDateTime.now())
				.updateUser(dto.getUpdateUser())
				.updateDate(LocalDateTime.now())
				.build();
		
//		togetherCommentRepository.save(entity);
		
		TogetherComment saved = togetherCommentRepository.save(entity);
		Long commentId = saved.getCommentId();

		var row = getTogetherCommentReceiverRepository.selectReceiversByCommentId(commentId);

		String boardReceiver = row == null ? null : row.getBoardReceiver();
		String parentReceiver = row == null ? null : row.getParentCommentReceiver();

        PushType pushType;
        List<String> receivers;

        if (dto.getParentCommentId() == null) {
            // 댓글 -> 게시글 작성자
            pushType = PushType.COMMENT_CREATED_TO;
            receivers = (boardReceiver == null) ? List.of() : List.of(boardReceiver);
        } else {
            // 대댓글 -> 부모댓글 작성자
            pushType = PushType.REPLY_CREATED_TO;
            receivers = (parentReceiver == null) ? List.of() : List.of(parentReceiver);
        }

        // 3) 본인 제외
        /*
        String me = currentUser.empNoOrThrow();
        receivers = receivers.stream().filter(r -> r != null && !r.equals(me)).toList();
        if (receivers.isEmpty()) return;
		*/
        
        // 4) payload data
        Map<String, Object> data = Map.of(
            "commenterNm", currentUser.nameKoreanOrThrow(),
            "boardId", dto.getBoardId()
        );

        // 5) 푸시 발송
        pushFacade.send(
            pushType,
            receivers,
            data,
            currentUser.empNoOrThrow()
        );
    }
	
	/**
     * 투게더 댓글 수정
     */
	@Transactional
	public void updateComment(Long boardId, TogetherCommentDto dto) {
		togetherCommentRepository.updateComment(boardId, dto.getCommentId(), dto.getContent(), dto.getUpdateUser());
	}
	
	/**
     * 투게더 댓글 삭제
     */
	@Transactional
	public void deleteComment(Long boardId, TogetherCommentDto dto) {
		togetherCommentRepository.deleteComment(boardId, dto.getCommentId(), dto.getUpdateUser());
	}
}
