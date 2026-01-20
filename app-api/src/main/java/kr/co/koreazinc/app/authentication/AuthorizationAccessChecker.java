package kr.co.koreazinc.app.authentication;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthorizationAccessChecker implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                      RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        HttpServletRequest request = context.getRequest();

        return new AuthorizationDecision(checkInternal(authentication, request));
    }

    private boolean checkInternal(Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            System.out.println("[ACCESS] auth=null");
            return false;
        }
        Object p = authentication.getPrincipal();
        System.out.println("[ACCESS] principalType=" + (p == null ? "null" : p.getClass().getName()));
        return (p instanceof UserDetails);
    }

}
