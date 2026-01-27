package kr.co.koreazinc.app.service.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubCommentDto;
import kr.co.koreazinc.app.model.detail.ClubDetailDto;
import kr.co.koreazinc.app.model.detail.ClubFeeInfoDto;
import kr.co.koreazinc.app.model.main.ClubJoinRequestDto;
import kr.co.koreazinc.app.model.main.ClubMemberDto;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.spring.util.CommonMap;
import kr.co.koreazinc.temp.model.converter.detail.ClubBoardConverter;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.model.entity.main.ClubJoinRequest;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import kr.co.koreazinc.temp.repository.comm.CommonMappingDocRepository;
import kr.co.koreazinc.temp.repository.detail.ClubBoardRepository;
import kr.co.koreazinc.temp.repository.detail.ClubCommentRepository;
import kr.co.koreazinc.temp.repository.detail.ClubDetailRepository;
import kr.co.koreazinc.temp.repository.detail.ClubJoinRequestRepository;
import kr.co.koreazinc.temp.repository.main.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubDetailService {
	
	private final ClubDetailRepository clubDetailRepository;
	private final ClubBoardRepository clubBoardRepository;
	private final ClubCommentRepository clubCommentRepository;
	private final CommonDocService commonDocService;
	private final ClubJoinRequestRepository clubJoinRequestRepository;
	private final CommonDocRepository commonDocRepository;
	private final CommonMappingDocRepository commonMappingDocRepository;
	private final ClubRepository clubRepository;
	
	/**
     * 동호회 상세 정보 조회
     */
	public ClubDetailDto.Get getClubDetail(Integer cludId) {
		// 기본 정보 조회
		ClubDetailDto.Get detail = clubDetailRepository
				.selectClubDetailView(ClubDetailDto.Get.class) // DTO 클래스 지정
				.eqCludId(cludId) // 조건 추가
				.fetchOne(); // 단건 조회
		
		if (detail != null) {
			// 공지사항 조회
			List<ClubBoardDto.Get> noticeList = clubBoardRepository.selectNoticeList(ClubBoardDto.Get.class, cludId);
			detail.setNotices(noticeList);
		}
		return detail;
	}
	
	/**
     * 동호회 게시물 조회
     */
	@Transactional
	public List<Map<String, Object>> getClubPostsList(Integer clubId) {
		List<ClubBoardDto.Get> posts = clubBoardRepository.selectClubPostsList(ClubBoardDto.Get.class, clubId);
		
		return posts.stream().map(post -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("boardId", post.getBoardId()); 
	        map.put("title", post.getTitle());
	        map.put("content", post.getContent());
	        map.put("authorNm", post.getAuthorNm());
	        map.put("authorPosition", post.getAuthorPosition());
	        map.put("createDate", post.getCreateDate());
	        map.put("commentCnt", post.getCommentCnt());
	        map.put("recommendCnt", post.getRecomendCnt());
	        map.put("viewCnt", post.getViewCnt());
	        
	        // 첨부파일 조회
			List<ClubBoardDto.FileDto> files = clubBoardRepository.selectPostFiles(ClubBoardDto.FileDto.class, post.getBoardId().longValue());
			files.forEach(file -> {
				// 이미지를 보여줄 경로 (mode = view)
				file.setDisplayUrl("/api/common/doc/download/CB/" +file.getDocNo() + "?mode=view");
				// 파일을 다운로드할 경로 (기본값 download)
		        file.setDownloadUrl("/api/common/doc/download/CB/" + file.getDocNo());
			});
			
			map.put("files", files);
	        return map;
	    }).collect(Collectors.toList());
	}
	
	/**
     * 동호회 게시물 추천하기
     */
	@Transactional
	public int updateRecommendPost(int boardId) {
		return clubBoardRepository.updateRecommendPost(boardId);
	}
	
	 /**
    * 동호회 게시글 조회수 증가
    */
	@Transactional
	public int updateViewCount(int boardId) {
		return clubBoardRepository.updateViewCount(boardId);
	}
	
	/**
     * 동호회 회비 조회
     */
	@Transactional
	public List<ClubFeeInfoDto.Get> getClubFeeInfoList(Integer clubId) {
		return clubDetailRepository.selectClubFeeInfoList(ClubFeeInfoDto.Get.class, clubId);
	}
	
	/**
     * 동호회 회비 수정
     */
	@Transactional
	public void updateClubFeeList(Integer clubId, List<ClubFeeInfoDto.Get> feeList, String empNo) {
		for (ClubFeeInfoDto.Get dto : feeList) {
			clubDetailRepository.updateClubFeeInfo(clubId, dto.getPositionCd(), dto.getPositionAmt(), empNo);
		}
	}
	
	/**
     * 동호회 게시글 작성 (Converter 기반 표준 방식)
     */
	@Transactional
	public boolean insertClubPost(ClubBoardDto.Get dto, List<MultipartFile> files, String empNo) {
		try {
			// 게시글 저장
			ClubBoard saveBoard = clubBoardRepository.insert(dto);
			int boardId = saveBoard.getBoardId();
			
			// 첨부파일 처리
			if (files != null && !files.isEmpty()) {
				for (MultipartFile file : files) {
					if(!file.isEmpty()) {
						// 파일을 DB에 바이너리로 저장 후 doc_no 획득
						Long docNo = commonDocService.saveFile(file, "CB", empNo);
						
						if (docNo != null) {
	                        commonDocService.saveMapping((long) boardId, docNo, empNo);
	                    }
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.error("게시글 저장 중 서버 에러 발생: {}", e.getMessage());
			return false;
		}
	}
	
	
	/**
     * 동호회 게시글 삭제 (Soft Delete)
     */
	@Transactional
	public boolean deleteClubPost(ClubBoardDto.Delete dto) {
		try {
			ClubBoard post = clubBoardRepository.findOne(dto.getBoardId());
			
			if(post == null) {
				log.error("삭제 실패: 게시글 존재하지 않음 (ID: {})", dto.getBoardId());
	            return false;
			}
			
			post.deletePost(dto.getUserEmpNo());
			
			/* if(dto.getBoardDocNo() != null) {
				Map<String, Object> param = new HashMap<>();
	            param.put("boardDocNo", dto.getBoardDocNo());
	            param.put("updateUser", dto.getUserEmpNo());
			} */
			return true;
		} catch (Exception e) {
			log.error("게시글 저장 중 서버 에러 발생: {}", e.getMessage());
			return false;
		}
	}
	
	/**
     * 동호회 게시글 수정
     */
	@Transactional
	public void updatePost(ClubBoardDto.Get dto, List<MultipartFile> files, String empNo) throws Exception {
		ClubBoard post = clubBoardRepository.findOne(dto.getBoardId());
		long boardId = Long.parseLong(String.valueOf(dto.getBoardId()));
		String jobSeCode = "CB";
		
		if (!post.getCreateUser().equals(empNo)) {
	        throw new SecurityException("본인이 작성한 글만 수정할 수 있습니다.");
	    }
		// 게시글 정보 업데이트
		post.update(
				dto.getTitle(),
		        dto.getContent(),
		        dto.getExpiryDate(),
		        dto.getIsNotice(),
		        empNo
		);
		
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
     * 동호회 게시글 상세보기
     */
	public ClubBoardDto.Get getClubPostDetail(Integer clubId, Integer boardId) {
		ClubBoardDto.Get detail = clubBoardRepository.selectClubPostDetail(ClubBoardDto.Get.class, boardId);
		
		if(detail == null || !detail.getClubId().equals(clubId)) {
			log.warn("조회 권한 없음 또는 게시글 없음: clubId={}, boardId={}", clubId, boardId);
			return null;
		}
		
		// 첨부파일 조회
		List<ClubBoardDto.FileDto> files = clubBoardRepository.selectPostFiles(ClubBoardDto.FileDto.class, boardId.longValue());
		
		files.forEach(file -> {
			// 이미지를 보여줄 경로 (mode = view)
			file.setDisplayUrl("/api/common/doc/download/CB/" + file.getDocNo() + "?mode=view");
			// 파일을 다운로드할 경로 (기본값 download)
	        file.setDownloadUrl("/api/common/doc/download/CB/" + file.getDocNo());
		});
		
		detail.setFiles(files);
		
		return detail;
	}
	
	
	/**
     * 동호회 댓글 조회
     */
	public List<ClubCommentDto> getCommentList(Long boardId) {
		return clubCommentRepository.selectCommentList(ClubCommentDto.class, boardId);
	}
	
	/**
     * 동호회 댓글 작성
     */
	@Transactional
	public void saveComment(ClubCommentDto dto) {
		clubCommentRepository.insert(dto);
	}
	
	/**
     * 동호회 댓글 수정
     */
	@Transactional
	public void updateComment(Long boardId, ClubCommentDto dto) {
		clubCommentRepository.updateComment(boardId, dto.getCommentId(), dto.getContent(), dto.getUpdateUser());
	}
	
	/**
     * 동호회 댓글 삭제
     */
	@Transactional
	public void deleteComment(Long boardId, ClubCommentDto dto) {
		clubCommentRepository.deleteComment(boardId, dto.getCommentId(), dto.getUpdateUser());
	}
	
	/**
     * 가입 요청 리스트 조회
     */
	@Transactional
	public List<ClubJoinRequestDto> getClubRequestList(Integer clubId) {
		return clubJoinRequestRepository.findRequestList(ClubJoinRequestDto.class, clubId);
    }
	
	/**
	 * 가입 승인/거절 처리
	 */
	@Transactional
	 public boolean updateJoinRequest(Map<String, Object> param) {
		Long clubId = Long.valueOf(param.get("clubId").toString());
		String requestEmpNo = (String) param.get("requestEmpNo");
		String status = (String) param.get("status");
		String updateUser = (String) param.get("updateUser");
		
		// 요청 상태 변경
		long updateCnt = clubJoinRequestRepository.updateJoinRequestStatus(clubId, requestEmpNo, status, updateUser);
		
		// 승인일 경우에만 멤버 추가
		if(status.equals("20")) {
			clubJoinRequestRepository.insertClubMember(clubId, requestEmpNo, updateUser);
		}
		return updateCnt > 0;
	}
	
	/**
     * 동호회 가입 명단 리스트 조회
     */
	@Transactional
	public List<ClubMemberDto> getClubMemberList(Integer clubId) {
		return clubRepository.selectClubMembers(ClubMemberDto.class, clubId);
	}
	
	/**
     * 동호회 가입 명단 리스트 제거
     */
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean deleteMembers(Map<String, Object> param) {
		Long clubId = Long.parseLong(String.valueOf(param.get("clubId")));
	    String updateUser = (String) param.get("updateUser");
	    
	    List<String> memberEmpNos = (List<String>) param.get("memberEmpNos");
	    if (memberEmpNos == null || memberEmpNos.isEmpty()) {
	        return false;
	    }
	    
	    long resultCnt = clubRepository.deleteClubMembers(clubId, memberEmpNos, updateUser);
	    return resultCnt > 0;
	}
}
