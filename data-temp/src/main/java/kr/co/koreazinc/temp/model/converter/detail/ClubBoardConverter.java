package kr.co.koreazinc.temp.model.converter.detail;

import kr.co.koreazinc.data.model.converter.EntityConverter;
import kr.co.koreazinc.temp.model.entity.detail.ClubBoard;

public class ClubBoardConverter extends EntityConverter<ClubBoard.Getter, ClubBoard> {
	
	public ClubBoardConverter(ClubBoard.Getter origin) {
		super(origin);
	}
	
	// 등록
	@Override
	public ClubBoard toEntity() {
        return ClubBoard.builder()
                .clubId(this.origin.getClubId())
                .title(this.origin.getTitle())
                .content(this.origin.getContent())
                .noticeYn(this.origin.getIsNotice() != null ? this.origin.getIsNotice() : "N")
                .noticeDt(this.origin.getExpiryDate())
                .viewCnt(0)
                .recommendCnt(0)
                .deleteYn("N")
                .createUser(this.origin.getUserEmpNo())
                .createDate(java.time.LocalDateTime.now())
                .updateUser(this.origin.getUserEmpNo())
                .updateDate(java.time.LocalDateTime.now())
                .build();
    }
	
	// 삭제
	public void toDeleteEntity(ClubBoard post, String empNo) {
		post.deletePost(empNo);
	}
}
