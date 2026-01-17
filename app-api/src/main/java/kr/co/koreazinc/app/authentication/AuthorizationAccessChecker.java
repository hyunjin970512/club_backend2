package kr.co.koreazinc.app.authentication;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorizationAccessChecker
        implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {
        return new AuthorizationDecision(this.check(authentication.get(), context.getRequest()));
    }

    private boolean check(Authentication authentication, HttpServletRequest request) {
        Object userDetails = authentication.getPrincipal();
        if (userDetails instanceof UserDetails) {
            return true;
        }
        return false;
    }
}
