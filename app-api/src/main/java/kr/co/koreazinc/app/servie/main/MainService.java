package kr.co.koreazinc.app.servie.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import kr.co.koreazinc.app.model.main.ApplyFeeRuleDetailDto;
import kr.co.koreazinc.app.model.main.AreaDto;
import kr.co.koreazinc.app.model.main.ClubListDto;
import kr.co.koreazinc.app.model.main.JoinedClubDto;
import kr.co.koreazinc.app.model.main.MenuDto;
import kr.co.koreazinc.temp.repository.main.ApplyFeeRuleRepository;
import kr.co.koreazinc.temp.repository.main.ClubRepository;
import kr.co.koreazinc.temp.repository.main.ClubUserCntRepository;
import kr.co.koreazinc.temp.repository.main.CommonCodeRepository;
import kr.co.koreazinc.temp.repository.main.MainClubRepository;
import kr.co.koreazinc.temp.repository.main.MenuRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

	// 메인 > 메뉴 조회
	private final MenuRepository menuRepository;
	
	// 메인 > 동호회 지원금 지급 규정
	private final ApplyFeeRuleRepository applyFeeRuleRepository;
	
	// 메인 > 가입한 동호회 개수 조회
	private final ClubUserCntRepository clubUserCntRepository;
	
	// 메인 > 가입한 동호회 목록 조회
	private final ClubRepository clubRepository;
	
	// 메인 > 동호회 목록 필터(칩) 조회
	private final CommonCodeRepository commonCodeRepository;
	
	// 메인 > 동호회 목록 조회 (ALL, KZ, OS)
	private final MainClubRepository mainClubRepository;

	// 메인 > 메뉴 조회
    public List<MenuDto.Get> getMenus(String empNo) {
        return menuRepository
            .selectMenusByEmpNo(MenuDto.Get.class)
            .eqEmpNo(empNo)
            .onlyUseY()
            .orderDefault()
            .fetch();
    }

    // 메인 > 동호회 지원금 지급 규정
    public List<ApplyFeeRuleDetailDto> getCurrentRules() {
        return applyFeeRuleRepository
            .selectCurrentRuleDetails(ApplyFeeRuleDetailDto.class)
            .orderByLineId()
            .fetch();
    }
    
    // 메인 > 가입한 동호회 개수 조회
    public int chkClubCnt(String empNo) {
        return clubUserCntRepository.countActiveClubsByEmpNo(empNo);
    }
    
    // 메인 > 가입한 동호회 목록 조회
    public List<JoinedClubDto.Get> getJoinedClubs(String empNo) {
        return clubRepository
            .selectJoinedClubs(JoinedClubDto.Get.class)
            .eqEmpNo(empNo)
            .fetch();
    }
    
    // 메인 > 동호회 목록 필터(칩) 조회
    public List<AreaDto> getAreas() {
        List<AreaDto> db = commonCodeRepository
            .selectAreas(AreaDto.class)
            .fetch();

        List<AreaDto> out = new ArrayList<>();
        out.add(new AreaDto("ALL", "전체"));
        out.addAll(db);
        return out;
    }
    
    // 메인 > 동호회 목록 조회 (ALL, KZ, OS)
    public List<ClubListDto> getClubs(String area) {
        return mainClubRepository.findClubList(ClubListDto.class, area);
    }


}
