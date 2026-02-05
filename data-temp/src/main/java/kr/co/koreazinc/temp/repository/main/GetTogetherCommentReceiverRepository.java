package kr.co.koreazinc.temp.repository.main;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.together.QTogetherBoard;
import kr.co.koreazinc.temp.model.entity.together.QTogetherComment;
import kr.co.koreazinc.temp.model.entity.together.TogetherComment;

import static kr.co.koreazinc.temp.model.entity.together.QTogetherBoard.togetherBoard;
import static kr.co.koreazinc.temp.model.entity.together.QTogetherComment.togetherComment;

@Repository
@Transactional(readOnly = true)
public class GetTogetherCommentReceiverRepository extends AbstractJpaRepository<TogetherComment, Long> {

    public GetTogetherCommentReceiverRepository(List<EntityManager> entityManagers) {
        super(TogetherComment.class, entityManagers);
    }

    /**
     * ✅ 투게더 댓글/대댓글 수신자 조회
     *
     * 동호회 레포 방식 그대로:
     * - 댓글이면: 게시글 작성자(boardReceiver)
     * - 대댓글이면: 부모댓글 작성자(parentCommentReceiver)
     *
     * 주의:
     * - parentCommentId가 null이어도 안전해야 함 => eq(null) 절대 안 탐
     *
     * SQL 느낌:
     * SELECT tb.create_user AS board_receiver,
     *        p.create_user  AS parent_comment_receiver
     * FROM together_comment tc
     * JOIN together_board tb ON tc.board_id = tb.board_id
     * LEFT JOIN together_comment p ON p.comment_id = tc.parent_comment_id
     * WHERE tb.delete_yn <> 'Y'
     *   AND tc.comment_id = :commentId;
     */
    public ReceiverRow selectReceiversByCommentId(Long commentId) {

        QTogetherComment parent = new QTogetherComment("parent");

        Tuple t = queryFactory
            .select(
                togetherBoard.createUser, // 게시글 작성자
                parent.createUser         // 부모댓글 작성자(없으면 null)
            )
            .from(togetherComment)
            .join(togetherComment.boardId, togetherBoard) // ✅ tc.board_id = tb.board_id (연관관계 필드명 boardId 기준)
            .leftJoin(parent).on(parent.commentId.eq(togetherComment.parentCommentId)) // ✅ null이어도 안전
            .where(
                togetherBoard.deleteYn.ne("Y"),
                togetherComment.commentId.eq(commentId)
            )
            .fetchOne();

        if (t == null) return null;

        String boardReceiver = t.get(togetherBoard.createUser);
        String parentCommentReceiver = t.get(parent.createUser);

        return new ReceiverRow(boardReceiver, parentCommentReceiver);
    }

    /**
     * - boardReceiver : 게시글 작성자 empNo
     * - parentCommentReceiver : 부모댓글 작성자 empNo (없으면 null)
     */
    public static class ReceiverRow {
        private final String boardReceiver;
        private final String parentCommentReceiver;

        public ReceiverRow(String boardReceiver, String parentCommentReceiver) {
            this.boardReceiver = boardReceiver;
            this.parentCommentReceiver = parentCommentReceiver;
        }

        public String getBoardReceiver() { return boardReceiver; }
        public String getParentCommentReceiver() { return parentCommentReceiver; }
    }
}
