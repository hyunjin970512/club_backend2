package kr.co.koreazinc.app.service.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import kr.co.koreazinc.app.model.admin.SubsidyManageDto;
import kr.co.koreazinc.temp.model.entity.admin.ClubApplyFeeManage;
import kr.co.koreazinc.temp.repository.admin.SubsidyManageRepository;

@Service
@RequiredArgsConstructor
public class SubsidyManageService {

  private final SubsidyManageRepository subsidyRepo;

  /** 조회 */
  @Transactional(readOnly = true)
  public List<SubsidyManageDto.Row> list(String year) {

    // 1) manage 있으면 그거 그대로
    if (subsidyRepo.existsManageByYear(year)) {
      return subsidyRepo
          .selectManageRows(SubsidyManageDto.Row.class, year)
          .fetch()
          .stream()
          .map(r -> new SubsidyManageDto.Row(
              r.getManageId(),
              r.getClubId(),
              r.getClubNm(),
              r.getClubLeader(),
              r.getMemberCnt(),
              r.getSupportAmount(),
              safeYn(r.getPayYn()),
              "MANAGE"
          ))
          .toList();
    }

    // 2) 없으면 계산(오늘 기준 규정 + 회원수 매칭을 DB에서 처리)
    return subsidyRepo
		.selectCalcRowsWithSupportAmount(SubsidyManageDto.RowCalc.class, year)
        .fetch()
        .stream()
        .map(r -> new SubsidyManageDto.Row(
            null,                 // manageId 없음
            r.getClubId(),
            r.getClubNm(),
            r.getClubLeader(),
            nz(r.getMemberCnt()),
            nz(r.getSupportAmount()),
            "N",                  // 계산모드 기본 미지급
            "CALC"
        ))
        .toList();
  }

  /** 저장(업서트): CALC 모드에서 프론트가 year + rows 보내는거 저장 */
  @Transactional
  public void save(String year, List<SubsidyManageDto.SaveRow> rows, String actorEmpNo) {
    if (rows == null || rows.isEmpty()) return;

    for (SubsidyManageDto.SaveRow r : rows) {
      if (r == null || r.getClubId() == null) continue;

      Long clubId = r.getClubId();
      Integer memberCnt = nz(r.getClubMemberCnt());
      Integer supportAmount = nz(r.getSupportAmount());
      String payYn = safeYn(r.getPayYn());

      // (clubId, year) 기준 업서트
      ClubApplyFeeManage entity = subsidyRepo.findManageByClubIdYear(clubId, year);

      if (entity == null) {
    	  entity = new ClubApplyFeeManage();
    	  entity.setClubId(clubId);
    	  entity.setYear(year);
    	  entity.setClubMemberCnt(memberCnt);
    	  entity.setSupportAmount(supportAmount);
    	  entity.setPayYn(payYn);
    	  entity.setCreateUser(actorEmpNo);
    	  entity.setCreateDate(LocalDateTime.now());
    	} else {
    	  entity.setClubMemberCnt(memberCnt);
    	  entity.setSupportAmount(supportAmount);
    	  entity.setPayYn(payYn);
    	  entity.setUpdateUser(actorEmpNo);
    	  entity.setUpdateDate(LocalDateTime.now());
    	}

    	// ✅ 신규든 수정이든 이 한 줄이면 끝
    	subsidyRepo.save(entity);

    }
  }

  private static int nz(Integer v) { return v == null ? 0 : v; }

  private static String safeYn(String v) {
    String s = (v == null ? "N" : v.trim().toUpperCase());
    return "Y".equals(s) ? "Y" : "N";
  }
}
