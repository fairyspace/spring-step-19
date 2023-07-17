package io.github.fairyspace.aop.framework.adapter;

import io.github.fairyspace.aop.AfterAdvice;
import io.github.fairyspace.aop.AfterReturningAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {
    private AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor() {
    }

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }
}
