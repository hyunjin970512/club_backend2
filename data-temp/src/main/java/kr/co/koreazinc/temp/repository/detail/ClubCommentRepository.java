package kr.co.koreazinc.temp.repository.detail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;

import jakarta.persistence.EntityManager;
import static kr.co.koreazinc.temp.model.entity.account.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.detail.QClubComment.clubComment;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.converter.detail.ClubCommentConverter;
import kr.co.koreazinc.temp.model.entity.detail.ClubComment;
import kr.co.koreazinc.temp.model.entity.detail.QClubComment;

@Repository
@Transactional(readOnly = true)
public class ClubCommentRepository extends AbstractJpaRepository<ClubComment, Long> {
	public ClubCommentRepository(@Autowired List<EntityManager> entityManagers) {
        super(ClubComment.class, entityManagers);
    }
	
	/**
     * 댓글 상세 조회 (DTO 프로젝션)
     */
	public <T> List<T> selectCommentList(Class<T> type, Long boardId) {
		QClubComment subComment = new QClubComment("subComment");
		
		return queryFactory
                .select(Projections.bean(type,
                        clubComment.commentId.as("commentId"),
                        clubComment.clubBoard.boardId.as("boardId"),
                        clubComment.parentCommentId.as("parentCommentId"),
                        clubComment.content.as("content"),
                        clubComment.recommendCnt.as("recommendCnt"),
                        coEmplBas.nameKo.as("authorNm"),
                        coEmplBas.positionCd.as("authorPosition"),
                        clubComment.createDate.as("createDate"),
                        clubComment.deleteYn.as("deleteYn")
                ))
                .from(clubComment)
                .leftJoin(coEmplBas).on(clubComment.createUser.eq(coEmplBas.empNo))
                .where(clubComment.clubBoard.boardId.eq(boardId.intValue())
                        .and(
                        	clubComment.deleteYn.eq("N")
                    		.or(
                    			// 삭제되었는데 자식이 존재하는 경우
                    			clubComment.deleteYn.eq("Y")
                    			.and(
                    				JPAExpressions
                    					.select(subComment.count())
                    					.from(subComment)
                    					.where(subComment.parentCommentId.eq(clubComment.commentId))
                    					.gt(0L)
                    			)
                    		)))
                .orderBy(clubComment.createDate.asc()) // 댓글은 작성순 정렬
                .fetch();
	}
	
	/**
     * 댓글 저장
     */
	@Transactional
    public ClubComment insert(ClubComment.Getter getter) {
		ClubComment entity = new ClubCommentConverter(getter).toEntity();
		return insert(entity);
	}
	
	/**
     * 댓글 수정
     */
	@Transactional
	public void updateComment (Long boardId, Long commentId, String content, String userEmpNo) {
		ClubComment entity = findOne(commentId);
		
		if (entity != null && entity.getClubBoard().getBoardId().equals(boardId.intValue())
				&& entity.getDeleteYn().equals("N")) {
			entity.updateComment(content, userEmpNo);
		}
	}
	
	/**
     * 댓글 삭제
     */
	@Transactional
	public void deleteComment (Long boardId, Long commentId, String userEmpNo) {
		ClubComment entity = findOne(commentId);
		
		if(entity != null && entity.getClubBoard().getBoardId().equals(boardId.intValue())) {
			entity.deleteComment(userEmpNo);
		}
	}
}