package kr.co.koreazinc.app.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private String empNo;
    private String role;
    
    public String getEmpNo() { return empNo; }
}
