package kr.co.koreazinc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static class ContextHolder {

        private static final ContextResource RESOURCE = new ContextResource();

        private ContextHolder() {
            super();
        }
    }

    private static final class ContextResource {

        private ApplicationContext context;

        private ContextResource() {
            super();
        }

        private void setContext(ApplicationContext context) {
            this.context = context;
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ContextHolder.RESOURCE.setContext(applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return ContextHolder.RESOURCE.context;
    }
}