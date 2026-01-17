package kr.co.koreazinc.spring.web.servlet.context;

import kr.co.koreazinc.spring.http.matcher.DomainRequestMatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class DomainContext {

    private final String context;

    private final DomainRequestMatcher matcher;
}