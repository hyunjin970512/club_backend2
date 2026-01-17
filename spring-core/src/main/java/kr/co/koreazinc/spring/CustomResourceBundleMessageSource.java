package kr.co.koreazinc.spring;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CustomResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    public Properties getMessages(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}