package kr.co.koreazinc.app.service.detail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.detail.ClubBoardDto;
import kr.co.koreazinc.app.model.detail.ClubDetailDto;
import kr.co.koreazinc.app.model.detail.ClubFeeInfoDto;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.repository.detail.ClubBoardRepository;
import kr.co.koreazinc.temp.repository.detail.ClubDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubDetailService {
	
	private final ClubDetailRepository clubDetailRepository;
	private final ClubBoardRepository clubBoardRepository;
	
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
	        map.put("createDate", post.getCreateDate());
	        map.put("commentCnt", post.getCommentCnt());
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
	public boolean insertClubPost(ClubBoardDto.Get dto) {
		try {
			clubBoardRepository.insert(dto);
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
}
