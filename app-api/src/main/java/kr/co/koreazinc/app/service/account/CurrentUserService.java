package kr.co.koreazinc.app.service.account;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.koreazinc.app.service.security.dto.UserPrincipal;

@Service
@Tag(name = "CurrentUserService", description = "현재 로그인한 사용자 정보 확인")
public class CurrentUserService {

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
}
