package kr.co.koreazinc.temp.repository.together;

import static kr.co.koreazinc.temp.model.entity.account.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.together.QTogetherComment.togetherComment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.together.QTogetherComment;
import kr.co.koreazinc.temp.model.entity.together.TogetherComment;

@Repository
public class TogetherCommentRepository extends AbstractJpaRepository<TogetherComment, Long> {
	public TogetherCommentRepository(@Autowired List<EntityManager> entityManagers) {
		super(TogetherComment.class, entityManagers);
	}
	
	/**
	* 댓글 조회
	*/
   public <T> List<T> selectCommentList(Class<T> type, Long boardId) {
	   QTogetherComment subComment = new QTogetherComment("subComment");
	   
	   return queryFactory
               .select(Projections.bean(type,
            		   togetherComment.commentId.as("commentId"),
            		   togetherComment.boardId.boardId.as("boardId"),
            		   togetherComment.parentCommentId.as("parentCommentId"),
            		   togetherComment.content.as("content"),
            		   togetherComment.recomendCnt.as("recommendCnt"),
                       coEmplBas.nameKo.as("authorNm"),
                       coEmplBas.positionCd.as("authorPosition"),
                       togetherComment.createDate.as("createDate"),
                       togetherComment.createUser.as("createUser"),
                       togetherComment.deleteYn.as("deleteYn")
               ))
               .from(togetherComment)
               .leftJoin(coEmplBas).on(togetherComment.createUser.eq(coEmplBas.empNo))
               .where(togetherComment.boardId.boardId.eq(boardId)
                       .and(
                    		togetherComment.deleteYn.eq("N")
                   		.or(
                   			// 삭제되었는데 자식이 존재하는 경우
                   			togetherComment.deleteYn.eq("Y")
                   			.and(
                   				JPAExpressions
                   					.select(subComment.count())
                   					.from(subComment)
                   					.where(subComment.parentCommentId.eq(togetherComment.commentId))
                   					.gt(0L)
                   			)
                   		)))
               .orderBy(togetherComment.createDate.asc())
               .fetch();
   }
   
   /**
    * 댓글 수정
    */
	@Transactional
	public void updateComment (Long boardId, Long commentId, String content, String userEmpNo) {
		TogetherComment entity = findOne(commentId);
		
		if (entity != null && entity.getBoardId().getBoardId().equals(boardId) && entity.getDeleteYn().equals("N")) {
			entity.updateComment(content, userEmpNo);
		}
	}

	/**
     * 댓글 삭제
     */
	@Transactional
	public void deleteComment (Long boardId, Long commentId, String userEmpNo) {
		TogetherComment entity = findOne(commentId);
		
		if (entity != null && entity.getBoardId().getBoardId().equals(boardId)) {
			entity.deleteComment(userEmpNo);
		}
	}
}
