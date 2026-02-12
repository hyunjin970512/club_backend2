package kr.co.koreazinc.app.service.account;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.koreazinc.app.model.main.JoinedClubDto;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import kr.co.koreazinc.temp.repository.main.ApplyFeeRuleRepository;
import kr.co.koreazinc.temp.repository.main.ClubRepository;
import kr.co.koreazinc.temp.repository.main.ClubUserCntRepository;
import kr.co.koreazinc.temp.repository.main.CommonCodeRepository;
import kr.co.koreazinc.temp.repository.main.MainClubRepository;
import kr.co.koreazinc.temp.repository.main.MenuRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Tag(name = "CurrentUserService", description = "현재 로그인한 사용자 정보 확인")
public class CurrentUserService {
	
	// 메인 > 가입한 동호회 목록 조회
	private final ClubRepository clubRepository;
	
    public UserPrincipal current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        return null;
    }

    public String empNoOrThrow() {
        UserPrincipal p = current();
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return p.getEmpNo();
    }
    
    public String nameKoreanOrThrow() {
        UserPrincipal p = current();
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return p.getNameKo();
    }
    
    public String userIdOrThrow() {
        UserPrincipal p = current();
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return p.getUserId();
    }
    
    public List<JoinedClubDto.Get> getJoinedClubs() {
        return clubRepository
            .selectJoinedClubs(JoinedClubDto.Get.class)
            .eqEmpNo(this.empNoOrThrow())
            .fetch();
    }
    
    public List<Long> getJoinedClubIds() {
        return getJoinedClubs().stream()
            .map(JoinedClubDto.Get::getClubId)
            .toList();
    }
    
    public List<Long> getJoinRequestClubIds() {
    	return clubRepository.selectJoinRequestClubIds(this.empNoOrThrow());
    }

}
