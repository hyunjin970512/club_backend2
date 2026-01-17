package kr.co.koreazinc.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

public class CustomBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation(annotatedBeanDefinition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return definition.getBeanClassName();
    }
}