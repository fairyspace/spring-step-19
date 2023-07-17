package io.github.fairyspace.aop.framework;

import io.github.fairyspace.aop.AdvisedSupport;

import java.lang.reflect.Method;
import java.util.List;

public interface AdvisorChainFactory {
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method, Class<?> targetClass);

}
