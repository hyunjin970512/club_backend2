package kr.co.koreazinc.temp.model.converter.detail;

import java.time.LocalDateTime;

import kr.co.koreazinc.data.model.converter.EntityConverter;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;
import kr.co.koreazinc.temp.model.entity.detail.ClubComment;

public class ClubCommentConverter extends EntityConverter<ClubComment.Getter, ClubComment> {
	
	public ClubCommentConverter(ClubComment.Getter origin) {
		super(origin);
	}
	
	@Override
	public ClubComment toEntity() {
		ClubBoard boardId = ClubBoard.builder()
				.boardId(origin.getBoardId())
				.build();
		
		// origin(DTO)에서 데이터를 꺼내 엔티티 빌더로 변환
		return ClubComment.builder()
				.clubBoard(boardId)
                .parentCommentId(origin.getParentCommentId())
                .content(origin.getContent())
                .createUser(origin.getCreateUser())
                .createDate(LocalDateTime.now())
                // 초기값 설정
                .recommendCnt(0)
                .deleteYn("N")
                .updateUser(origin.getCreateUser())
                .updateDate(LocalDateTime.now())
                .build();
	}
}
