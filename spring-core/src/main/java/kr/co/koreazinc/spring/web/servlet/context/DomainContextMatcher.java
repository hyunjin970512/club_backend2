package kr.co.koreazinc.spring.web.servlet.context;

import java.util.Set;

public interface DomainContextMatcher {

    public Set<DomainContext> getDomainContexts();
}