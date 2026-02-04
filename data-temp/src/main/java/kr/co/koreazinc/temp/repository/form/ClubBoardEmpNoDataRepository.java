package kr.co.koreazinc.temp.repository.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;

import jakarta.persistence.EntityManager;

import kr.co.koreazinc.data.repository.AbstractJpaRepository;

import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.model.entity.detail.QClubComment;

import static kr.co.koreazinc.temp.model.entity.detail.QClubBoard.clubBoard;
import static kr.co.koreazinc.temp.model.entity.detail.QClubComment.clubComment;

@Repository
@Transactional(readOnly = true)
public class ClubBoardEmpNoDataRepository extends AbstractJpaRepository<ClubBoard, Long> {

    public ClubBoardEmpNoDataRepository(@Autowired List<EntityManager> entityManagers) {
        super(ClubBoard.class, entityManagers);
    }

    /**
     * SQL:
     * SELECT cb.club_id,
     *        cb.create_user AS board_receiver,
     *        p.create_user  AS comment_receiver
     * FROM club_board cb
     * JOIN club_comment cc ON cc.board_id = cb.board_id
     * LEFT JOIN club_comment p ON p.comment_id = cc.parent_comment_id
     * WHERE cb.delete_yn <> 'Y'
     *   AND cc.comment_id = :commentId;
     *
     * @return [clubId, boardReceiver, parentCommentReceiver]
     */
    public ReceiverRow selectReceiversByCommentId(Long commentId) {
        QClubComment parent = new QClubComment("parent");

        Tuple t = queryFactory
                .select(
                        clubBoard.clubId,        // cb.club_id
                        clubBoard.createUser,    // cb.create_user
                        parent.createUser        // p.create_user
                )
                .from(clubComment)
                .join(clubComment.clubBoard, clubBoard) // cc.board_id = cb.board_id
                .leftJoin(parent).on(parent.commentId.eq(clubComment.parentCommentId))
                .where(
                        clubBoard.deleteYn.ne("Y"),
                        clubComment.commentId.eq(commentId)
                )
                .fetchOne();

        if (t == null) return null;

        Integer clubId = t.get(clubBoard.clubId);
        String boardReceiver = t.get(clubBoard.createUser);
        String parentCommentReceiver = t.get(parent.createUser);

        return new ReceiverRow(clubId, boardReceiver, parentCommentReceiver);
    }

    /**
     * - clubId
     * - boardReceiver : 게시글 작성자 empNo
     * - commentReceiver : 부모댓글 작성자 empNo (없으면 null)
     */
    public static class ReceiverRow {
        private final Integer clubId;
        private final String boardReceiver;
        private final String commentReceiver;

        public ReceiverRow(Integer clubId, String boardReceiver, String commentReceiver) {
            this.clubId = clubId;
            this.boardReceiver = boardReceiver;
            this.commentReceiver = commentReceiver;
        }

        public Integer getClubId() { return clubId; }
        public String getBoardReceiver() { return boardReceiver; }
        public String getCommentReceiver() { return commentReceiver; }
    }
}
