package kr.co.koreazinc.spring.utility;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import kr.co.koreazinc.spring.CustomResourceBundleMessageSource;
import kr.co.koreazinc.spring.util.CommonMap;

@Component
public class MessageUtils {

    private static class ContextHolder {

        private static final ContextResource RESOURCE = new ContextResource();

        private ContextHolder() {
            super();
        }
    }

    private static final class ContextResource {

        private MessageSource context;

        private ContextResource() {
            super();
        }

        private void setContext(MessageSource context) {
            this.context = context;
        }
    }

    @Autowired
    private void setMessageSource(MessageSource messageSource) {
        ContextHolder.RESOURCE.setContext(messageSource);
    }

    public static String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return ContextHolder.RESOURCE.context.getMessage(code, args, locale);
    }

    public static String getMessage(String code, Locale locale) throws NoSuchMessageException {
        return MessageUtils.getMessage(code, null, locale);
    }

    public static CommonMap getMessages(Locale locale) {
        CustomResourceBundleMessageSource messageSource = (CustomResourceBundleMessageSource) ContextHolder.RESOURCE.context;
        CommonMap messages = new CommonMap();
        Properties allMessages = messageSource.getMessages(locale);
        Set<Map.Entry<Object, Object>> entries = allMessages.entrySet();
        for(Map.Entry<Object, Object> entry : entries) {
            messages.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return messages;
    }
}