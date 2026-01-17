package kr.co.koreazinc.spring.web.servlet.result.view;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.koreazinc.spring.web.servlet.result.view.Modeling.RedirectBuilder;

public class DefaultModelingBuilder implements Modeling.RedirectBuilder {

    @Nullable
    private final Modeling model;

    private RedirectAttributes attributes;

    DefaultModelingBuilder(String viewName) {
        this.model = new Modeling(viewName);
    }

    DefaultModelingBuilder(Modeling model) {
        this.model = model;
    }

    @Override
    public DefaultModelingBuilder modelAttribute(String name, Object value) {
        this.model.addObject(name, value);
        return this;
    }

    @Override
    public DefaultModelingBuilder modelAttribute(Object value) {
        this.model.addObject(value);
        return this;
    }

    @Override
    public DefaultModelingBuilder modelAttributes(Object... values) {
        for (Object value : values) {
            this.model.addObject(value);
        }
        return this;
    }

    @Override
    public DefaultModelingBuilder model(Map<String, ?> map) {
        this.model.addAllObjects(map);
        return this;
    }

    @Override
    public DefaultModelingBuilder status(HttpStatusCode status) {
        this.model.setStatus(status);
        return this;
    }

    @Override
    public Modeling build() {
        return this.model;
    }

    @Override
    public RedirectBuilder redirectAttributes(RedirectAttributes attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public RedirectBuilder redirectAttribute(String name, Object value) {
        this.attributes.addAttribute(name, value);
        return this;
    }

    @Override
    public RedirectBuilder redirectFlashAttribute(String name, Object value) {
        this.attributes.addFlashAttribute(name, value);
        return this;
    }
}