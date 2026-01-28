package kr.co.koreazinc.app.service.security.dto;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class UserPrincipal implements UserDetails {

	private final String empNo;   // 사번
	private final String accId;   // 계정아이디
	private final String nameKo;   // 계정이름
	private final Collection<? extends GrantedAuthority> authorities;
	
	public UserPrincipal(String empNo, String accId,String nameKo,
	    Collection<? extends GrantedAuthority> authorities) {
	  this.empNo = empNo;
	  this.accId = accId;
	  this.nameKo = nameKo;
	  this.authorities = authorities;
	}
	
	public String getEmpNo() { return empNo; }
	public String getAccId() { return accId; }
	public String getNameKo() { return nameKo; }
	
	@Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
	@Override public String getPassword() { return ""; }
	@Override public String getUsername() { return empNo != null ? empNo : accId; }
	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return true; }

}
