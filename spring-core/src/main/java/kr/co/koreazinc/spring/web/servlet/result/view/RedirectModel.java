package kr.co.koreazinc.spring.web.servlet.result.view;

import org.springframework.http.HttpStatus;

public class RedirectModel extends Modeling {

    public RedirectModel(String viewName) {
        super("redirect:" + viewName, HttpStatus.FOUND);
    }
}