package io.github.fairyspace.aop.framework;

import io.github.fairyspace.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advised;
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), advised.getTargetSource().getTargetClass(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target = advised.getTargetSource().getTarget();
        Class<?> targetClass = target.getClass();

        // 获取拦截器链
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        Object retVal = null;
        if(chain==null||chain.isEmpty()){
            return method.invoke(target, args);
        }else{
            // 将拦截器统一封装成ReflectiveMethodInvocation
            MethodInvocation invocation = new ReflectiveMethodInvocation(target, method, args, chain);
            // Proceed to the joinpoint through the interceptor chain.
            // 执行拦截器链
            retVal = invocation.proceed();
        }
        return retVal;

    }

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }
}
