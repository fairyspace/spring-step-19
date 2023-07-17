package io.github.fairyspace.aop.framework;

import io.github.fairyspace.aop.AdvisedSupport;
import io.github.fairyspace.aop.Advisor;
import io.github.fairyspace.aop.MethodMatcher;
import io.github.fairyspace.aop.PointcutAdvisor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultAdvisorChainFactory implements AdvisorChainFactory{
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method, Class<?> targetClass) {
        Advisor[] advisors = config.getAdvisors().toArray(new Advisor[0]);
        List<Object> interceptorList = new ArrayList<>(advisors.length);
        Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                // Add it conditionally.
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                // 校验当前Advisor是否适用于当前对象
                if (pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    boolean  match = mm.matches(method, actualClass);
                    // 校验Advisor是否应用到当前方法上
                    if (match) {
                        MethodInterceptor interceptor = (MethodInterceptor) advisor.getAdvice();
                        interceptorList.add(interceptor);
                    }
                }
            }
        }
        return interceptorList;
    }
}
