package kr.co.koreazinc.spring.web.servlet.result.view;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class Modeling extends ModelAndView {

    public Modeling(String viewName) {
        super(viewName);
    }

    public Modeling(String viewName, HttpStatusCode status) {
        super(viewName, status);
    }

    public static Builder<?> view(String name) {
        return new DefaultModelingBuilder(name);
    }

    public static RedirectBuilder redirectTo(String url) {
        return new DefaultModelingBuilder(new RedirectModel(url)).redirectAttributes(new RedirectAttributesModelMap());
    }

    public static RedirectBuilder redirectTo(String url, RedirectAttributes attributes) {
        return new DefaultModelingBuilder(new RedirectModel(url)).redirectAttributes(attributes);
    }

    public static interface Builder<B extends Builder<B>> {

        B modelAttribute(String name, Object value);

        B modelAttribute(Object value);

        B modelAttributes(Object... values);

        B model(Map<String, ?> map);

        B status(HttpStatusCode status);

        Modeling build();
    }

    public static interface RedirectBuilder extends Builder<RedirectBuilder> {

        RedirectBuilder redirectAttributes(RedirectAttributes attributes);

        RedirectBuilder redirectAttribute(String name, Object value);

        RedirectBuilder redirectFlashAttribute(String name, Object value);
    }
}