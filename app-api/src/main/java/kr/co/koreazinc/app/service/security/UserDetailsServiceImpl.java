package kr.co.koreazinc.app.service.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.service.security.dto.UserPrincipal;
import kr.co.koreazinc.temp.model.chk.Employee;
import kr.co.koreazinc.temp.repository.chk.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository; // ✅ 주입

    @Override
    public UserDetails loadUserByUsername(String empNo) throws UsernameNotFoundException {

        Employee e = employeeRepository
                .findByEmpNoAndUseAtAndDeleteAt(empNo, "Y", "N")
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found empNo=" + empNo));

        return new UserPrincipal(
                e.getEmpNo(),
                empNo,
                e.getNameKo(),
                e.getUserId(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
