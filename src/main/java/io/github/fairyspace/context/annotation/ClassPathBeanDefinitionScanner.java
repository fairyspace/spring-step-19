package io.github.fairyspace.context.annotation;

import cn.hutool.core.util.StrUtil;
import io.github.fairyspace.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import io.github.fairyspace.beans.factory.config.BeanDefinition;
import io.github.fairyspace.beans.factory.support.BeanDefinitionRegistry;
import io.github.fairyspace.stereotype.Component;

import java.util.Set;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider{

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "io.github.fairyspace.beans.factory.annotation.internalAutowiredAnnotationProcessor";

    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidates) {
                // 解析 Bean 的作用域 singleton、prototype
                String beanScope = resolveBeanScope(beanDefinition);
                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }
                String beanName = determineBeanName(beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        //注册处理@Autowired和@Value注解的BeanPostProcessor
        registry.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (null != scope) return scope.value();
        return StrUtil.EMPTY;
    }

    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }

}
