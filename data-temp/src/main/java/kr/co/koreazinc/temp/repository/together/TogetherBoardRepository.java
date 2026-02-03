package kr.co.koreazinc.temp.repository.together;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;

import static kr.co.koreazinc.temp.model.entity.account.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.comm.QCommonDoc.commonDoc;
import static kr.co.koreazinc.temp.model.entity.comm.QCommonMappingDoc.commonMappingDoc;
import static kr.co.koreazinc.temp.model.entity.together.QTogetherBoard.togetherBoard;

@Repository
public class TogetherBoardRepository extends AbstractJpaRepository<TogetherBoard, Long> {
	public TogetherBoardRepository(@Autowired List<EntityManager> entityManagers) {
		super(TogetherBoard.class, entityManagers);
	}
	
	/**
	* 게시글 상세 조회
	*/
	public <T> T selectTogetherPostDetail(Class<T> type, Long boardId) {
		return queryFactory
                .select(Projections.bean(type,
                		togetherBoard.boardId,
                        togetherBoard.title,
                        togetherBoard.content,
                        coEmplBas.nameKo.as("authorNm"),
                        coEmplBas.positionCd.as("authorPosition"),
                        togetherBoard.createDate.as("createDate"),
                        togetherBoard.createUser.as("createUser"),
                        togetherBoard.viewCnt,
                        togetherBoard.recomendCnt
                ))
                .from(togetherBoard)
                .leftJoin(coEmplBas).on(coEmplBas.empNo.eq(togetherBoard.createUser))
                .where(togetherBoard.boardId.eq(boardId)
	                    .and(togetherBoard.deleteYn.eq("N")))
                .fetchOne();
	}
	
	/**
    * 첨부파일 목록 조회
    */
   public <T> List<T> selectPostFiles(Class<T> type, Long boardId) {
	   return queryFactory
	            .select(Projections.bean(type,
	                    commonDoc.docNo,
	                    commonDoc.docFileNm,
	                    commonDoc.createUser,
	                    commonDoc.createDate
	            ))
	            .from(commonDoc)
	            .join(commonMappingDoc).on(commonDoc.docNo.eq(commonMappingDoc.docNo))
	            .where(
	                    commonMappingDoc.refId.eq(boardId), 
	                    commonMappingDoc.deleteYn.eq("N"),
	                    commonDoc.deleteYn.eq("N"),
	                    commonDoc.jobSeCode.eq("TO")
	            )
	            .fetch();
   }
   
   /**
	* 게시글 조회수 증가
	*/
   public int updateViewCount(Long boardId) {
	   // 1. 조회수 1 증가 (Update 쿼리 수행)
	   long affectedRows = queryFactory
	            .update(togetherBoard)
	            .set(togetherBoard.viewCnt, togetherBoard.viewCnt.add(1))
	            .where(togetherBoard.boardId.eq(boardId).and(togetherBoard.deleteYn.eq("N")))
	            .execute();
	   
	   if (affectedRows == 0) {
		   throw new RuntimeException("해당 게시글을 찾을 수 없거나 삭제된 상태입니다.");
	   }
	   
	   // 2. 갱신된 조회수 재조회
	   Integer lastViewCnt = queryFactory
	            .select(togetherBoard.viewCnt)
	            .from(togetherBoard)
	            .where(togetherBoard.boardId.eq(boardId))
	            .fetchOne();
	   
	   return lastViewCnt != null ? lastViewCnt : 0;
   }
   
   /**
    * 게시글 추천하기
    */
   public int updateRecommendPost(Long boardId) {
	   // 1. 추천수 증가
	   int affectedRows = (int) queryFactory
	            .update(togetherBoard)
	            .set(togetherBoard.recomendCnt, togetherBoard.recomendCnt.add(1))
	            .where(togetherBoard.boardId.eq(boardId).and(togetherBoard.deleteYn.eq("N")))
	            .execute();
	   
	   if (affectedRows == 0) {
	        throw new RuntimeException("해당 게시글을 찾을 수 없거나 삭제된 상태입니다.");
	   }
	   
	   // 2. 갱신 추천수 재조회
	   Integer lastCnt = queryFactory
	            .select(togetherBoard.recomendCnt)
	            .from(togetherBoard)
	            .where(togetherBoard.boardId.eq(boardId))
	            .fetchOne();
	  
	   return lastCnt != null ? lastCnt : 0;
   }
}