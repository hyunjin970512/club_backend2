package kr.co.koreazinc.temp.repository.detail;

import static kr.co.koreazinc.temp.model.entity.account.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.detail.QClubBoard.clubBoard;
import static kr.co.koreazinc.temp.model.entity.detail.QClubComment.clubComment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.converter.detail.ClubBoardConverter;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;

@Repository
@Transactional
public class ClubBoardRepository extends AbstractJpaRepository<ClubBoard, Integer> {
	public ClubBoardRepository(@Autowired List<EntityManager> entityManagers) {
        super(ClubBoard.class, entityManagers);
    }

    /**
     * 공지사항 리스트 조회
     */
    public <T> List<T> selectNoticeList(Class<T> type, Integer clubId) {
        return queryFactory
                .select(Projections.bean(type,
                        clubBoard.boardId.as("noticeBoardId"),
                        clubBoard.title.as("noticeTitle"),
                        clubBoard.content.as("noticeContent")
                ))
                .from(clubBoard)
                .where(clubBoard.clubId.eq(clubId)
                        .and(clubBoard.noticeYn.eq("Y"))
                        .and(clubBoard.deleteYn.eq("N")))
                .orderBy(clubBoard.createDate.desc())
                .limit(3)
                .fetch();
    }
    
    /**
     * 게시글 리스트 조회
     */
   public <T> List<T> selectClubPostsList(Class<T> type, Integer clubId) {
    	return queryFactory
                .select(Projections.bean(type,
                        clubBoard.boardId,
                        clubBoard.title,
                        clubBoard.content,
                        coEmplBas.nameKo.as("authorNm"),
                        coEmplBas.positionCd.as("authorPosition"),
                        clubBoard.createDate.as("createDate"),
                        clubBoard.viewCnt,
                        clubBoard.recomendCnt,
                        clubComment.commentId.count().intValue().as("commentCnt")
                ))
                .from(clubBoard)
                // 작성자 조인 (CB.create_user = CEB.emp_no)
                .leftJoin(coEmplBas).on(coEmplBas.empNo.eq(clubBoard.createUser))
                // 댓글 조인 (CB.board_id = CC.board_id)
                .leftJoin(clubComment).on(clubComment.clubBoard.boardId.eq(clubBoard.boardId).and(clubComment.deleteYn.eq("N")))
                .where(clubBoard.clubId.eq(clubId)
                        .and(clubBoard.deleteYn.eq("N")))
                .groupBy(clubBoard.boardId, coEmplBas.nameKo, coEmplBas.positionCd)
                .orderBy(clubBoard.createDate.desc())
                .fetch();
    }
   
   /**
    * 게시글 추천하기
    */
   public int updateRecommendPost(int boardId) {
	   // 1. 추천수 증가
	   int affectedRows = (int) queryFactory
	            .update(clubBoard)
	            .set(clubBoard.recomendCnt, clubBoard.recomendCnt.add(1))
	            .where(clubBoard.boardId.eq(boardId).and(clubBoard.deleteYn.eq("N")))
	            .execute();
	   
	   if (affectedRows == 0) {
	        throw new RuntimeException("해당 게시글을 찾을 수 없거나 삭제된 상태입니다.");
	   }
	   
	   // 2. 갱신 추천수 재조회
	   Integer lastCnt = queryFactory
	            .select(clubBoard.recomendCnt)
	            .from(clubBoard)
	            .where(clubBoard.boardId.eq(boardId))
	            .fetchOne();
	  
	   return lastCnt != null ? lastCnt : 0;
   }
   
   /**
    * 게시글 조회수 증가하기
    */
   public int updateViewCount(int boardId) {
	// 1. 조회수 1 증가 (Update 쿼리 수행)
	    long affectedRows = queryFactory
	            .update(clubBoard)
	            .set(clubBoard.viewCnt, clubBoard.viewCnt.add(1))
	            .where(clubBoard.boardId.eq(boardId).and(clubBoard.deleteYn.eq("N")))
	            .execute();
	    
	    if (affectedRows == 0) {
	        throw new RuntimeException("해당 게시글을 찾을 수 없거나 삭제된 상태입니다.");
	    }
	    
	    // 2. 갱신된 조회수 재조회
	    Integer lastViewCnt = queryFactory
	            .select(clubBoard.viewCnt)
	            .from(clubBoard)
	            .where(clubBoard.boardId.eq(boardId))
	            .fetchOne();
	  
	    return lastViewCnt != null ? lastViewCnt : 0;
   }
   
   /**
    * 동호회 게시글 저장하기 (표준 Converter 방식 Insert)
    * ClubBoard.Getter 인터페이스를 구현한 어떤 객체(DTO)든 인자로 받을 수 있음
    */
   public ClubBoard insert(ClubBoard.Getter getter) {
	   ClubBoard entity = new ClubBoardConverter(getter).toEntity(); // DTO(getter)를 엔티티로 변환
 	  return insert(entity);
   }
   
   /**
    * 게시글 상세보기
    */
   /**
    * 게시글 상세 조회 (Fluent API 방식)
    */
   public <T> T selectClubPostDetail(Class<T> type, Integer boardId) {
	   return queryFactory
	            .select(Projections.bean(type,
	                    clubBoard.boardId,
	                    clubBoard.clubId,
	                    clubBoard.title,
	                    clubBoard.content,
	                    clubBoard.noticeYn.as("isNotice"),   // DTO 필드명에 맞춤
	                    clubBoard.noticeDt.as("expiryDate"), // DTO 필드명에 맞춤
	                    clubBoard.createUser,
	                    clubBoard.createDate,
	                    clubBoard.viewCnt,
	                    clubBoard.recomendCnt,
	                    coEmplBas.nameKo.as("authorNm")
	            ))
	            .from(clubBoard)
	            .leftJoin(coEmplBas).on(coEmplBas.empNo.eq(clubBoard.createUser))
	            .where(clubBoard.boardId.eq(boardId)
	                    .and(clubBoard.deleteYn.eq("N")))
	            .fetchOne();
   }
}