package kr.co.koreazinc.spring.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import kr.co.koreazinc.spring.security.exception.UserJoinFailedException;
import kr.co.koreazinc.spring.security.model.UserInfo;

public interface ExpandUserDetailsService extends UserDetailsService {

    default boolean isJoin() {
        return true;
    }

	default UserDetails loadUserByUserInfo(UserInfo userInfo) throws UsernameNotFoundException {
        return null;
    }

    UserDetails joinUser(UserInfo userInfo) throws UserJoinFailedException;
}