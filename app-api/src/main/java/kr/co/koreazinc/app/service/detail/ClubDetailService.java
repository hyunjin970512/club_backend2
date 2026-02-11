package kr.co.koreazinc.app.service.detail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.detail.ClubAuthDto;
import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubCommentDto;
import kr.co.koreazinc.app.model.detail.ClubDetailDto;
import kr.co.koreazinc.app.model.detail.ClubFeeInfoDto;
import kr.co.koreazinc.app.model.detail.ClubGwRequest;
import kr.co.koreazinc.app.model.main.ClubJoinRequestDto;
import kr.co.koreazinc.app.model.main.ClubMemberDto;
import kr.co.koreazinc.app.model.push.PushType;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.app.service.push.PushFacade;
import kr.co.koreazinc.spring.util.CommonMap;
import kr.co.koreazinc.temp.model.converter.detail.ClubBoardConverter;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.model.entity.detail.ClubComment;
import kr.co.koreazinc.temp.model.entity.main.ClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.ClubJoinRequest;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.repository.comm.CommonDocRepository;
import kr.co.koreazinc.temp.repository.comm.CommonMappingDocRepository;
import kr.co.koreazinc.temp.repository.detail.ClubBoardRepository;
import kr.co.koreazinc.temp.repository.detail.ClubCommentRepository;
import kr.co.koreazinc.temp.repository.detail.ClubDetailRepository;
import kr.co.koreazinc.temp.repository.detail.ClubJoinRequestRepository;
import kr.co.koreazinc.temp.repository.form.ClubBoardEmpNoDataRepository;
import kr.co.koreazinc.temp.repository.form.ClubCreateRequestRepository;
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
	private final ClubCreateRequestRepository clubCreateRequestRepository;
	
	private final ClubBoardEmpNoDataRepository clubBoardEmpNoDataRepository;

	//push
	private final PushFacade pushFacade;
	
	private final CurrentUserService currentUser;
	
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
	        map.put("authorEmpNo", post.getCreateUser());
	        
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
			clubDetailRepository.saveClubFeeInfo(clubId, dto.getPositionCd(), dto.getPositionAmt(), empNo);
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
			
			if(boardId != 0) {
				
				String clubName = this.getClubDetail(dto.getClubId()).getClubName();
				
				List<ClubMemberDto> sendList = new ArrayList<ClubMemberDto>();
				
				sendList = this.getClubMemberList(dto.getClubId());
				
				for(int i = 0; i < sendList.size(); i++) {
					
					String sendEmpno = sendList.get(i).getEmpNo();
					
					Map<String, Object> data = Map.of(
							"clubNm", clubName,
							"authorNm", currentUser.nameKoreanOrThrow(),
							"postTitle", dto.getTitle(),
							"clubId", dto.getClubId(),
							"postId", boardId
						);
					
					pushFacade.send(
							PushType.POST_CREATED,
							List.of(sendEmpno),
							data,
							empNo // createdByEmpNo
						);
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
			// 게시물, 댓글 삭제
			post.deletePost(dto.getUserEmpNo());
			// 첨부파일 삭제
			List<CommonMappingDoc> mapping = commonMappingDocRepository.findByRefId((long) dto.getBoardId());
			
			if (mapping != null) {
				for (CommonMappingDoc map : mapping) {
					commonDocService.deleteFile((long) dto.getBoardId(), map.getDocNo(), "CB", dto.getUserEmpNo());
				}
			}
			
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
		
//		clubCommentRepository.insert(dto);
		
		ClubComment saved = clubCommentRepository.insert(dto);
		Long commentId = saved.getCommentId();
		String boardId = String.valueOf(dto.getBoardId());
		Integer clubId;
		
		var row = clubBoardEmpNoDataRepository.selectReceiversByCommentId(Long.valueOf(commentId));
		
		// 게시판 작성자
		String boardReceiver = row.getBoardReceiver();
		// 댓글 작성자
		String parentCommentReceiver = row.getCommentReceiver();
		// 동호회 아이디
		clubId = row.getClubId();
		
		PushType pushType;
		List<String> receivers;
		
		
		if (dto.getParentCommentId() == null) {
			// 댓글: 게시글 작성자에게
			pushType = PushType.COMMENT_CREATED;
			receivers = (boardReceiver == null) ? List.of() : List.of(boardReceiver);
		} else {
			// 대댓글: 부모댓글 작성자에게
			pushType = PushType.REPLY_CREATED;
			receivers = (parentCommentReceiver == null) ? List.of() : List.of(parentCommentReceiver);
		}
		
		Map<String, Object> data = Map.of(
				"commenterNm", currentUser.nameKoreanOrThrow(),
				"clubId", clubId,
				"postId", boardId
				);
		
		pushFacade.send(
				pushType,
				receivers,
				data,
				currentUser.empNoOrThrow()
			);
		
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
		
		// 동호회명 조회
		String clubName = this.getClubDetail(Math.toIntExact(clubId)).getClubName();
		PushType pushType = null;
		
		Map<String, Object> data = Map.of(
				"clubNm", clubName,
				"clubId", clubId
			);
		
		// 요청 상태 변경
		long updateCnt = clubJoinRequestRepository.updateJoinRequestStatus(clubId, requestEmpNo, status, updateUser);
		
		// 승인일 경우에만 멤버 추가
		if(status.equals("20")) {
			clubJoinRequestRepository.insertClubMember(clubId, requestEmpNo, updateUser);
			pushType = PushType.CLUB_JOIN_APPROVED;
		// 가입 거절
		}else if(status.equals("30")) {
			pushType = PushType.CLUB_JOIN_REJECTED;
		}
		
		pushFacade.send(
				pushType,
				List.of(requestEmpNo),
				data,
				currentUser.empNoOrThrow()
				);
		
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
	
	/**
     * 동호회 탈퇴
     */
	@Transactional
	public boolean quitClub(Integer clubId, String empNo) {
		long updatedCount = clubRepository.quitClub(clubId, empNo);
        return updatedCount > 0;
	}
	
	/**
     * 동호회 권한 정보 조회
     */
	@Transactional
	public ClubAuthDto getClubAuthInfo(Long clubId, String empNo) {
		// 회원 정보 조회
		Optional<ClubUserInfo> userInfo = clubDetailRepository.getClubAuthInfo(clubId, empNo);
		
		// 가입 신청 상태 조회
		String joinStatus = clubDetailRepository.getJoinRequestStatus(clubId, empNo).orElse(null);
		
		return userInfo.map(info -> {
	        // 회원인 경우
	        return new ClubAuthDto(info.getUserRoleCd(), info.getStatus(), info.getEmpNo(), joinStatus);
	    }).orElseGet(() -> {
	        // 회원이 아닌 경우 (GUEST 권한 부여 및 가입 신청 상태 포함)
	        return new ClubAuthDto("GUEST", "00", empNo, joinStatus);
	    });
	}
	
	/**
     * 동호회 정보 수정 (목적, 파일)
     */
	@Transactional
	public void updateClubRequestInfo(ClubGwRequest dto, String empNo) {
		ClubCreateRequest request = clubCreateRequestRepository.findById(dto.getRequestId())
	            .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));
		
		
		request.setPurpose(dto.getPurpose());
	    request.setRuleFileId(dto.getRuleFileId());
	    request.setMemberFileId(dto.getMemberFileId());
	    request.setUpdateUser(empNo);
	    request.setUpdateDate(LocalDateTime.now());
	    
	    clubCreateRequestRepository.save(request);
	}
	
	/**
     * 동호회 신설 GW 상신 데이터
     */
	public String createXmlData(ClubGwRequest dto, String serverBaseUrl) {
		String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
	    String formattedPurpose = "";
	    
	    if (dto.getPurpose() != null) {
	    	// \n 또는 \r\n을 모두 <br>로 치환
	        formattedPurpose = dto.getPurpose().replace("\n", "<br>").replace("\r", "");
	    }

	    StringBuilder xml = new StringBuilder();
	    xml.append("<Document>");
	    xml.append("<Title>동호회 등록 신청서(").append(dto.getClubName()).append(")</Title>");
	    xml.append("<Items>");
	    xml.append("<Item>");
	    xml.append("<ClubName>").append(dto.getClubName()).append("</ClubName>");
	    xml.append("<ClubId>").append(dto.getClubId()).append("</ClubId>");
	    xml.append("<RequestId>").append(dto.getRequestId()).append("</RequestId>");
	    xml.append("<CreateDate>").append(today).append("</CreateDate>");
	    xml.append("<MemberCnt>").append(dto.getMemberCnt()).append("명</MemberCnt>");
	    xml.append("<MemberDept>");
	    xml.append("대표자소속 : ").append(dto.getDeptCd()).append("팀 / ");
	    xml.append("직위 : ").append(dto.getPositionCd()).append(" / ");
	    xml.append("성명 : ").append(dto.getRequestNm());
	    xml.append("</MemberDept>");
	    xml.append("</Item>");
	    xml.append("</Items>");
	    xml.append("<Purpose><![CDATA[").append(formattedPurpose).append("]]></Purpose>");
	    // ■ 파일 리스트 처리
	    xml.append("<Files>");
	    // 1. 동호회 회칙
	    xml.append("<File>");
        xml.append("<FileName>1. 동호회 회칙 1부</FileName>");
        xml.append("<FileLink>").append(serverBaseUrl).append("/api/common/doc/download/FR/").append(dto.getRuleFileId()).append("</FileLink>");
        xml.append("</File>");
	    // 2. 회원 명부
        xml.append("<File>");
        xml.append("<FileName>2. 회원명부 1부</FileName>");
        xml.append("<FileLink>").append(serverBaseUrl).append("/api/common/doc/download/MB/").append(dto.getMemberFileId()).append("</FileLink>");
        xml.append("</File>");
	    xml.append("</Files>");
	    
	    xml.append("<RequestClub>").append(dto.getClubName()).append("</RequestClub>");
	    xml.append("<RequestNm>").append(dto.getRequestNm()).append("</RequestNm>");
	    xml.append("</Document>");

	    return xml.toString().replaceAll("&", "&amp;").replaceAll("%", "&#37;");
	}
	
	/**
     * 동호회 GW 결재 상태 연동
     */
	@Transactional
	public void callSpClubGwAfter(int cludId, int requestId, String gwDocNo, String status, String userId) {
		try {
			clubDetailRepository.executeClubGwAfter(cludId, requestId, gwDocNo, status, userId);
		} catch (Exception e) {
			log.error("프로시저 호출 중 오류 발생: {}", e.getMessage());
            throw e;
		}
	}
}
